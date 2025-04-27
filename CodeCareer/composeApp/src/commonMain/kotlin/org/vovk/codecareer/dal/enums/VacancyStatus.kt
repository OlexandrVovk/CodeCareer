package org.vovk.codecareer.dal.enums

import kotlinx.serialization.Serializable

@Serializable
enum class VacancyStatus(val displayName: String) {
    INTERESTED("Interested"),
    RESUME_SENT("Resume sent"),
    INTERVIEW_SCHEDULED("Interview scheduled"),
    OFFER("Offer"),
    REJECTED("Rejected"),
    NOT_INTERESTED("Not interested")
}