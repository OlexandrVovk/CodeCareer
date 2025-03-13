package org.vovk.codecareer.dal.vacancies

import androidx.compose.runtime.mutableStateListOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.vovk.codecareer.dal.entities.JobCartEntity

external object VacanciesObject : JsAny {
    var vacanciesString: String
    fun updateVacancies(newString: String)
}

@Serializable
data class JobRaw(
    val jobTitle: String = "",
    val url: String = "",
    val company: String = "",
    val description: String = "",
    val companyImage: String = ""
)

object VacanciesEntityManager {
    private val _jobs = mutableStateListOf<JobCartEntity>()
    fun getVacancies(): List<JobCartEntity> = _jobs

    fun parseJsonToJobCartEntityList(json: String): List<JobCartEntity> {
        // Decode the JSON string to a list of JobRaw objects.
        val jobRawList = try {
            Json { ignoreUnknownKeys = true }.decodeFromString<List<JobRaw>>(json)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
        // Map the raw jobs to JobCartEntity instances.
        val jobList = jobRawList.map {
            JobCartEntity(
                companyName = it.company,
                companyUrl = it.companyImage,
                jobName = it.jobTitle,
                jobDescription = it.description,
                tags = emptyList(),
            )
        }
        _jobs.clear()
        _jobs.addAll(jobList)
        return jobList
    }
}