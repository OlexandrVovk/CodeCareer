package org.vovk.codecareer.ui.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.vovk.codecareer.dal.enums.EmploymentTypes
import org.vovk.codecareer.dal.enums.JobCategories
import org.vovk.codecareer.dal.enums.WorkingExperience

@Composable
fun FilterSection() {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Категорія", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        JobCategoryFilterButtons()

        Spacer(modifier = Modifier.height(16.dp))

        Text("Зарплата", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Slider(value = 0.5f, onValueChange = {})

        Spacer(modifier = Modifier.height(16.dp))
        Text("Досвід роботи", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        WorkingExperienceFilterButtons()

        Spacer(modifier = Modifier.height(16.dp))
        Text("Employment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        EmploymentFilterButtons()
    }
}

@Composable
fun FilterCategory(text: String) {
    Box(
        modifier = Modifier
            .background(Color.Gray.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JobCategoryFilterButtons() {
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        JobCategories.entries.forEach { category ->
            FilterCategory(category.name)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkingExperienceFilterButtons() {
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WorkingExperience.entries.forEach { category ->
            FilterCategory(category.name)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EmploymentFilterButtons() {
    FlowRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EmploymentTypes.entries.forEach { category ->
            FilterCategory(category.name)
        }
    }
}
