package org.vovk.codecareer.dal.entities

import org.vovk.codecareer.dal.enums.EmploymentType
import org.vovk.codecareer.dal.enums.JobCategory
import org.vovk.codecareer.dal.enums.WorkingExperience

data class FilterDataEntity(
    val categories: List<JobCategory> = emptyList(),
    val experiences: List<WorkingExperience> = emptyList(),
    val employmentTypes: List<EmploymentType> = emptyList()
)