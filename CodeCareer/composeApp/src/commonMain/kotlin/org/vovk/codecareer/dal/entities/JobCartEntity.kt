package org.vovk.codecareer.dal.entities

import kotlinx.serialization.Serializable
import org.vovk.codecareer.dal.enums.VacancyStatus

@Serializable
data class JobCartEntity(
    val companyName: String,
    val companyImageUrl: String,
    val jobName: String,
    val jobDescription: String,
    val jobUrl: String
)

@Serializable
data class TrackedVacancy(
    val jobInfo: JobCartEntity,
    var status: VacancyStatus = VacancyStatus.INTERESTED,
    var notes: String = "",
    var interviewSchedules: List<InterviewSchedule> = emptyList()
)

