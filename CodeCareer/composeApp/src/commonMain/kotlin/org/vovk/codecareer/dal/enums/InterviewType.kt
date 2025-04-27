package org.vovk.codecareer.dal.enums

/**
 * Enum representing different types of interviews in the recruitment process.
 */
enum class InterviewType(val displayName: String) {
    SCREENING_CALL("Screening Call"),
    HR_INTERVIEW("HR Interview"),
    TECHNICAL_INTERVIEW("Technical Interview"),
    INTERVIEW_WITH_HIRING_MANAGER("Interview with Hiring Manager"),
    FINAL_INTERVIEW("Final Interview");

    companion object {
        fun fromDisplayName(displayName: String): InterviewType? {
            return values().find { it.displayName == displayName }
        }
    }
}