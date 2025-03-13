package org.vovk.codecareer.dal.entities

data class JobCartEntity(
    val companyName: String,
    val companyImageUrl: String,
    val jobName: String,
    val jobDescription: String,
    val jobUrl: String,
    val tags: List<String>
)