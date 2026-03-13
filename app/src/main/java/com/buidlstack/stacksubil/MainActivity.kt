package com.buidlstack.stacksubil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import com.buidlstack.stacksubil.ui.navigation.AppNavigation
import com.buidlstack.stacksubil.ui.navigation.Routes
import com.buidlstack.stacksubil.ui.theme.BuildStackTheme
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val app = application as BuildStackApplication
            val theme by app.userPreferences.theme.collectAsState(initial = "dark")
            var hasSeenOnboarding by remember { mutableStateOf<Boolean?>(null) }

            LaunchedEffect(Unit) {
                hasSeenOnboarding = app.userPreferences.hasSeenOnboarding.first()
            }

            BuildStackTheme(appTheme = theme) {
                val onboardingState = hasSeenOnboarding
                if (onboardingState != null) {
                    AppNavigation(
                        startDestination = if (onboardingState) Routes.MAIN else Routes.SPLASH
                    )
                }
            }
        }
    }
}
