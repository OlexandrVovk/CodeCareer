package org.vovk.codecareer.dal.entities

import kotlinx.serialization.Serializable
import org.vovk.codecareer.dal.enums.InterviewType

@Serializable
data class InterviewSchedule(
    val date: String = "",  // Format: "YYYY-MM-DD"
    val time: String = "",  // Format: "HH:MM"
    val type: InterviewType? = null,
    val notes: String = ""
)
