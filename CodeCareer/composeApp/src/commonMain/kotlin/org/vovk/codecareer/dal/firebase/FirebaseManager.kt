package org.vovk.codecareer.dal.firebase

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.vovk.codecareer.dal.entities.JobCartEntity
import org.vovk.codecareer.dal.entities.TrackedVacancy
import org.vovk.codecareer.dal.entities.InterviewSchedule
import org.vovk.codecareer.dal.enums.VacancyStatus

// Auth JS functions declaration
external fun createUserWithEmail(email: String, password: String, displayName: String, callback: (String) -> Unit)
external fun signInWithEmail(email: String, password: String, callback: (String) -> Unit)
external fun handleGoogleLogin(callback: (String) -> Unit)
external fun signOut()

// Vacancies JS functions declaration
external fun addNewVacancyTrack(companyName: String,
                                companyImageUrl: String,
                                jobName: String,
                                jobDescription: String,
                                jobUrl: String,
                                callback: (String) -> Unit)
external fun getTrackedVacancies(callback: (String) -> Unit)
external fun updateTrackedVacancy(vacancyUrl:String, status:String, notes: String, callback: (Boolean) -> Unit)
external fun deleteTrackedVacancy(vacancyUrl:String, callback: (Boolean) -> Unit)
external fun scheduleInterview(vacancyUrl:String, dateAndTime: String, type: String, notes: String, callback: (Boolean) -> Unit)
external fun deleteMeeting(vacancyUrl:String, dateAndTime: String, callback: (Boolean) -> Unit)

class FirebaseManager {

    // Trigger Google login
    fun loginWithGoogle(onSuccess: () -> Unit) {
        handleGoogleLogin { userDataJson ->
            UserSessionManager.saveUserSession(userDataJson)
            onSuccess()
        }
    }

    fun registerWithEmail(
        email: String,
        password: String,
        displayName: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        createUserWithEmail(email, password, displayName) { response ->
            if (response.contains("\"success\":true")) {
                callback(true, null)
            } else {
                // Parse error message
                val errorStart = response.indexOf("\"message\":\"")
                val errorMessage = if (errorStart >= 0) {
                    val start = errorStart + 11 // length of "message":"
                    val end = response.indexOf("\"", start)
                    if (end >= 0) response.substring(start, end) else "Unknown error"
                } else {
                    "Registration failed"
                }
                callback(false, errorMessage)
            }
        }
        println("Registering with email: $email")
    }

    fun signInWithEmail(
        email: String,
        password: String,
        callback: (success: Boolean, errorMessage: String?) -> Unit
    ) {
        signInWithEmail(email, password) { response ->
            if (response.contains("\"success\":true")) {
                // Success - save the user session
                UserSessionManager.saveUserSession(response)
                callback(true, null)
            } else {
                // Parse error message
                val errorStart = response.indexOf("\"message\":\"")
                val errorMessage = if (errorStart >= 0) {
                    val start = errorStart + 11 // length of "message":"
                    val end = response.indexOf("\"", start)
                    if (end >= 0) response.substring(start, end) else "Unknown error"
                } else {
                    "Registration failed"
                }
                callback(false, errorMessage)
            }
        }
    }

    // Sign out the current user
    fun toSignOut() {
        signOut()
        UserSessionManager.clearUserSession()
    }

    fun toAddNewVacancyTrack(vacancy: JobCartEntity){
        addNewVacancyTrack(
            companyName = vacancy.companyName,
            companyImageUrl = vacancy.companyImageUrl,
            jobName = vacancy.jobName,
            jobDescription = vacancy.jobDescription,
            jobUrl = vacancy.jobUrl
        ){response ->
            println(response)
        }
    }

    fun toGetTrackedVacancies(callback: (List<TrackedVacancy>) -> Unit) {
        getTrackedVacancies { response ->
            try {
                // Parse the JSON response
                val jsonObject = Json.parseToJsonElement(response).jsonObject

                // Check if the request was successful
                val isSuccess = jsonObject["success"]?.jsonPrimitive?.boolean ?: false

                if (isSuccess) {
                    // Extract the vacancies array
                    val vacanciesArray = jsonObject["vacancies"]?.jsonArray

                    if (vacanciesArray != null) {
                        // Parse each vacancy in the array
                        val trackedVacancies = vacanciesArray.mapNotNull { vacancyElement ->
                            try {
                                val vacancyObj = vacancyElement.jsonObject

                                // Extract job info
                                val jobInfoObj = vacancyObj["jobInfo"]?.jsonObject
                                if (jobInfoObj != null) {
                                    val jobCartEntity = JobCartEntity(
                                        companyName = jobInfoObj["companyName"]?.jsonPrimitive?.content ?: "",
                                        companyImageUrl = jobInfoObj["companyImageUrl"]?.jsonPrimitive?.content ?: "",
                                        jobName = jobInfoObj["jobName"]?.jsonPrimitive?.content ?: "",
                                        jobDescription = jobInfoObj["jobDescription"]?.jsonPrimitive?.content ?: "",
                                        jobUrl = jobInfoObj["jobUrl"]?.jsonPrimitive?.content ?: ""
                                    )

                                    // Extract status and notes
                                    val statusString = vacancyObj["status"]?.jsonPrimitive?.content ?: "INTERESTED"
                                    val status = try {
                                        VacancyStatus.valueOf(statusString)
                                    } catch (e: IllegalArgumentException) {
                                        VacancyStatus.INTERESTED // Default if invalid status
                                    }

                                    val notes = vacancyObj["notes"]?.jsonPrimitive?.content ?: ""

                                    // Parse multiple interview schedules if present
                                    val interviewSchedules: List<org.vovk.codecareer.dal.entities.InterviewSchedule> =
                                        (vacancyObj["interviewSchedules"] as? JsonArray)?.mapNotNull { scheduleElement ->
                                            try {
                                                val scheduleObj = scheduleElement.jsonObject
                                                val date = scheduleObj["date"]?.jsonPrimitive?.content ?: ""
                                                val time = scheduleObj["time"]?.jsonPrimitive?.content ?: ""
                                                val typeString = scheduleObj["type"]?.jsonPrimitive?.content
                                                val type = try {
                                                    typeString?.let { org.vovk.codecareer.dal.enums.InterviewType.valueOf(it) }
                                                } catch (_: IllegalArgumentException) {
                                                    null
                                                }
                                                val scheduleNotes = scheduleObj["notes"]?.jsonPrimitive?.content ?: ""
                                                org.vovk.codecareer.dal.entities.InterviewSchedule(
                                                    date = date,
                                                    time = time,
                                                    type = type,
                                                    notes = scheduleNotes
                                                )
                                            } catch (e: Exception) {
                                                println("Error parsing interview schedule: ${e.message}")
                                                null
                                            }
                                        } ?: run {
                                            // Fallback to singular interviewSchedule field for backward compatibility
                                            (vacancyObj["interviewSchedule"] as? JsonObject)?.let { obj ->
                                                val date = obj["date"]?.jsonPrimitive?.content ?: ""
                                                val time = obj["time"]?.jsonPrimitive?.content ?: ""
                                                val typeString = obj["type"]?.jsonPrimitive?.content
                                                val type = try {
                                                    typeString?.let { org.vovk.codecareer.dal.enums.InterviewType.valueOf(it) }
                                                } catch (_: IllegalArgumentException) {
                                                    null
                                                }
                                                val scheduleNotes = obj["notes"]?.jsonPrimitive?.content ?: ""
                                                listOf(
                                                    org.vovk.codecareer.dal.entities.InterviewSchedule(
                                                        date = date,
                                                        time = time,
                                                        type = type,
                                                        notes = scheduleNotes
                                                    )
                                                )
                                            } ?: emptyList()
                                        }

                                    // Create TrackedVacancy object
                                    TrackedVacancy(
                                        jobInfo = jobCartEntity,
                                        status = status,
                                        notes = notes,
                                        interviewSchedules = interviewSchedules
                                    )
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                println("Error parsing vacancy: ${e.message}")
                                null
                            }
                        }

                        println("Successfully parsed ${trackedVacancies.size} tracked vacancies")
                        callback(trackedVacancies)
                    } else {
                        println("No vacancies array found in response")
                        callback(emptyList())
                    }
                } else {
                    // Handle error
                    val errorMessage = jsonObject["message"]?.jsonPrimitive?.content
                    println("Error retrieving tracked vacancies: $errorMessage")
                    callback(emptyList())
                }
            } catch (e: Exception) {
                println("Error parsing tracked vacancies response: ${e.message}")
                callback(emptyList())
            }
        }
    }

    fun toUpdateTrackedVacancy(updatedVacancy: TrackedVacancy): Boolean {
        var result = false
        updateTrackedVacancy(updatedVacancy.jobInfo.jobUrl,
            updatedVacancy.status.toString(),
            updatedVacancy.notes
        ){response -> result = response}
        return result
    }

    fun toScheduleInterview(updatedVacancy: TrackedVacancy): Boolean {
        var result = false
        // Use the last interview schedule in the list
        val schedule = updatedVacancy.interviewSchedules.lastOrNull() ?: return false
        val dateAndTime = schedule.date + "_" + schedule.time
        scheduleInterview(
            vacancyUrl = updatedVacancy.jobInfo.jobUrl,
            dateAndTime = dateAndTime,
            type = schedule.type.toString(),
            notes = schedule.notes
        ) { response -> result = response }
        return result
    }

    /**
     * Deletes a specific interview meeting in Firebase.
     * @param vacancy The tracked vacancy.
     * @param schedule The interview schedule to delete.
     */
    fun toDeleteMeeting(vacancy: TrackedVacancy, schedule: InterviewSchedule): Boolean {
        var result = false
        val dateAndTime = schedule.date + "_" + schedule.time
        deleteMeeting(
            vacancyUrl = vacancy.jobInfo.jobUrl,
            dateAndTime = dateAndTime
        ) { response -> result = response }
        return result
    }

    fun toDeleteTrackedVacancy(updatedVacancy: TrackedVacancy): Boolean {
        var result: Boolean = false
        deleteTrackedVacancy(updatedVacancy.jobInfo.jobUrl){
            response -> result = response
        }
        return result
    }
}
