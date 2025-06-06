package org.vovk.codecareer.dal.filters

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.vovk.codecareer.dal.entities.FilterDataEntity
import org.vovk.codecareer.dal.enums.EmploymentType
import org.vovk.codecareer.dal.enums.JobCategory
import org.vovk.codecareer.dal.enums.WorkingExperience
import kotlin.collections.joinToString
import kotlinx.serialization.json.Json

external fun sendRequest(text: String) : String


class FilterStateManager {
    private val selectedCategories: SnapshotStateList<JobCategory> = mutableStateListOf()
    private val selectedExperiences: SnapshotStateList<WorkingExperience> = mutableStateListOf()
    private val selectedEmploymentTypes: SnapshotStateList<EmploymentType> = mutableStateListOf()

    fun toggleCategory(category: JobCategory) {
        if (selectedCategories.contains(category)) {
            selectedCategories.remove(category)
        } else {
            selectedCategories.add(category)
        }
    }

    fun toggleExperience(experience: WorkingExperience) {
        if (selectedExperiences.contains(experience)) {
            selectedExperiences.remove(experience)
        } else {
            selectedExperiences.add(experience)
        }
    }

    fun toggleEmploymentType(employmentType: EmploymentType) {
        if (selectedEmploymentTypes.contains(employmentType)) {
            selectedEmploymentTypes.remove(employmentType)
        } else {
            selectedEmploymentTypes.add(employmentType)
        }
    }

    fun isCategorySelected(category: JobCategory): Boolean {
        return selectedCategories.contains(category)
    }

    fun isExperienceSelected(experience: WorkingExperience): Boolean {
        return selectedExperiences.contains(experience)
    }

    fun isEmploymentTypeSelected(employmentType: EmploymentType): Boolean {
        return selectedEmploymentTypes.contains(employmentType)
    }

    fun getFilterDataEntity(): FilterDataEntity {
        return FilterDataEntity(
            categories = selectedCategories.toList(),
            experiences = selectedExperiences.toList(),
            employmentTypes = selectedEmploymentTypes.toList()
        )
    }

    fun applyFilters() {
        val filterDataEntity = getFilterDataEntity()
        val params = mutableMapOf<String, String>()

        if (filterDataEntity.categories.isNotEmpty()) {
            val languages = filterDataEntity.categories.joinToString(separator = ",") { it.name.lowercase() }
            params["language"] = languages
        }

        if (filterDataEntity.experiences.isNotEmpty()) {
            val experiences = filterDataEntity.experiences.joinToString(separator = ",") { it.name.lowercase() }
            params["exp"] = experiences
        }

        if (filterDataEntity.employmentTypes.isNotEmpty()) {
            val employments = filterDataEntity.employmentTypes.joinToString(separator = ",") { it.name.lowercase().replace(" ", "") }
            params["employment"] = employments
        }
        sendRequest(Json.encodeToString(params))
    }

    fun clearAllFilters() {
        selectedCategories.clear()
        selectedExperiences.clear()
        selectedEmploymentTypes.clear()
    }
}