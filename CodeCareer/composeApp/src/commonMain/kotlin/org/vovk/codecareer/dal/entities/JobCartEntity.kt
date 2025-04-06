package org.vovk.codecareer.dal.entities

import kotlinx.serialization.Serializable

@Serializable
data class JobCartEntity(
    val companyName: String,
    val companyImageUrl: String,
    val jobName: String,
    val jobDescription: String,
    val jobUrl: String
)

@Serializable
enum class VacancyStatus(val displayName: String) {
    INTERESTED("Interested"),
    RESUME_SENT("Resume sent"),
    INTERVIEW_SCHEDULED("Interview scheduled"),
    OFFER("Offer"),
    REJECTED("Rejected"),
    NOT_INTERESTED("Not interested")
}

@Serializable
data class TrackedVacancy(
    val jobInfo: JobCartEntity,
    var status: VacancyStatus = VacancyStatus.INTERESTED,
    var notes: String = ""
)

