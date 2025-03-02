package org.vovk.codecareer.dal.entities

data class JobCartEntity(
    val firmName: String,
    val jobName: String,
    val jobDescription: String,
    val tags: List<String>,
    val salary: String
)