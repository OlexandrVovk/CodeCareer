package org.vovk.codecareer.dal.entities

data class JobCartEntity(
    val companyName: String,
    val companyUrl: String,
    val jobName: String,
    val jobDescription: String,
    val tags: List<String>
)