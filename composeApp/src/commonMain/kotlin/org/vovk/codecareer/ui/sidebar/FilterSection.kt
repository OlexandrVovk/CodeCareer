package org.vovk.codecareer.ui.sidebar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FilterSection() {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text("Категорія", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        RepeatFilterButtons(4)

        Spacer(modifier = Modifier.height(16.dp))

        Text("Зарплата", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Slider(value = 0.5f, onValueChange = {})

        Spacer(modifier = Modifier.height(16.dp))

        Text("Досвід роботи", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        RepeatFilterButtons(3)
    }
}

@Composable
fun RepeatFilterButtons(count: Int) {
    Column {
        repeat(count) {
            Box(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .background(Color.Gray.copy(alpha = 0.4f))
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text("Фільтр $it", fontSize = 14.sp)
            }
        }
    }
}