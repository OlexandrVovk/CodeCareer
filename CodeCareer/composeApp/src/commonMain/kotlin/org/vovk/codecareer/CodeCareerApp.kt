package org.vovk.codecareer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.browser.window
import org.vovk.codecareer.dal.vacancies.VacanciesEntityManager
import org.vovk.codecareer.dal.vacancies.VacanciesObject
import org.vovk.codecareer.pages.JobSearchScreen
import org.vovk.codecareer.pages.LoginPage
import org.vovk.codecareer.pages.RegisterPage
import org.vovk.codecareer.ui.navbar.CodeCareerTopAppBar
import org.w3c.dom.events.Event

@Composable
fun App(){
    MainScreenWrapper()
    initEventListeners()
}

fun initEventListeners() {
    val vacanciesUpdatedListener: (Event) -> Unit = { event ->
        VacanciesEntityManager.parseJsonToJobCartEntityList(VacanciesObject.vacanciesString)
    }
    window.addEventListener("vacanciesUpdated", vacanciesUpdatedListener)
}

@Composable
fun MainScreenWrapper() {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val screenWidth = maxWidth
        val isCompactScreen = screenWidth < 1024.dp
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isCompactScreen) Color.White else Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(1024.dp)
                    .fillMaxHeight()
                    .background(Color.White)
            ) {
                Navigator(screen = JobSearchScreen()) { navigator: Navigator ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        CodeCareerTopAppBar(
                            onNavigateToJobs = {
                                navigator.push(JobSearchScreen())
                            },
                            onNavigateToLogin = {
                                navigator.push(LoginPage())
                            },
                            onNavigateToRegister = {
                                navigator.push(RegisterPage())
                            }
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            val currentScreen = navigator.lastItem
                            currentScreen.Content()
                        }
                    }
                }
            }
        }
    }
}
