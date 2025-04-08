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
import org.vovk.codecareer.pages.JobSearchPage
import org.vovk.codecareer.pages.TracksPage
import org.vovk.codecareer.pages.auth.LoginPage
import org.vovk.codecareer.pages.auth.RegisterPage
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(15,15,17,255)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(1024.dp)
                    .fillMaxHeight()
                    .background(Color(15,15,17,255))
            ) {
                Navigator(screen = JobSearchPage()) { navigator: Navigator ->
                    Column(modifier = Modifier.fillMaxSize()) {
                        CodeCareerTopAppBar(
                            onNavigateToJobs = {
                                navigator.push(JobSearchPage())
                            },
                            onNavigateToLogin = {
                                navigator.push(LoginPage())
                            },
                            onNavigateToRegister = {
                                navigator.push(RegisterPage())
                            },
                            onNavigateToTracks = {
                                navigator.push(TracksPage())
                            },
                            navigator = navigator
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
