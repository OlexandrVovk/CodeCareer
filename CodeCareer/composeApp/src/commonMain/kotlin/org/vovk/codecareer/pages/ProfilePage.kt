package org.vovk.codecareer.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen

class ProfilePage : Screen{
    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize().background(color = Color.Red)) {
            Text("ProfilePage")
        }
    }
}