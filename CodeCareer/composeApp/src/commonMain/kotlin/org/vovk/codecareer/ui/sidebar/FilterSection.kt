package org.vovk.codecareer.ui.sidebar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.dal.enums.EmploymentType
import org.vovk.codecareer.dal.enums.JobCategory
import org.vovk.codecareer.dal.enums.WorkingExperience
import org.vovk.codecareer.dal.filters.FilterStateManager

@Composable
fun FilterSection() {
    // Create a FilterStateManager to handle filter state
    val filterStateManager = remember { FilterStateManager() }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Категорія", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        JobCategoryFilterButtons(filterStateManager)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Досвід роботи", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        WorkingExperienceFilterButtons(filterStateManager)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Employment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        EmploymentFilterButtons(filterStateManager)
    }
}

@Composable
fun FilterCategoryButton(
    text: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (isSelected) Color.Green else Color.Gray
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCategoryFilterButtons(filterStateManager: FilterStateManager) {
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JobCategory.entries.forEach { category ->
            val isSelected = filterStateManager.isCategorySelected(category)
            FilterCategoryButton(
                text = category.name,
                isSelected = isSelected,
                onToggle = { filterStateManager.toggleCategory(category) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkingExperienceFilterButtons(filterStateManager: FilterStateManager) {
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WorkingExperience.entries.forEach { experience ->
            val isSelected = filterStateManager.isExperienceSelected(experience)
            FilterCategoryButton(
                text = experience.name,
                isSelected = isSelected,
                onToggle = { filterStateManager.toggleExperience(experience) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmploymentFilterButtons(filterStateManager: FilterStateManager) {
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EmploymentType.entries.forEach { employmentType ->
            val isSelected = filterStateManager.isEmploymentTypeSelected(employmentType)
            FilterCategoryButton(
                text = employmentType.name,
                isSelected = isSelected,
                onToggle = { filterStateManager.toggleEmploymentType(employmentType) }
            )
        }
    }
}