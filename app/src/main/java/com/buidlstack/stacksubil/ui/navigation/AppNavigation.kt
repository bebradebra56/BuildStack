package com.buidlstack.stacksubil.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.buidlstack.stacksubil.grfed.presentation.app.BuildStackApplication
import com.buidlstack.stacksubil.ui.screens.calculators.CalculatorsScreen
import com.buidlstack.stacksubil.ui.screens.converter.ConverterScreen
import com.buidlstack.stacksubil.ui.screens.dashboard.DashboardScreen
import com.buidlstack.stacksubil.ui.screens.history.HistoryScreen
import com.buidlstack.stacksubil.ui.screens.measurements.AddMeasurementScreen
import com.buidlstack.stacksubil.ui.screens.measurements.MeasurementDetailScreen
import com.buidlstack.stacksubil.ui.screens.measurements.MeasurementsScreen
import com.buidlstack.stacksubil.ui.screens.measurements.MeasurementsViewModel
import com.buidlstack.stacksubil.ui.screens.onboarding.OnboardingScreen
import com.buidlstack.stacksubil.ui.screens.projects.AddProjectScreen
import com.buidlstack.stacksubil.ui.screens.projects.ProjectDetailScreen
import com.buidlstack.stacksubil.ui.screens.projects.ProjectsScreen
import com.buidlstack.stacksubil.ui.screens.projects.ProjectsViewModel
import com.buidlstack.stacksubil.ui.screens.settings.SettingsScreen
import com.buidlstack.stacksubil.ui.screens.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val MAIN = "main"
    const val DASHBOARD = "dashboard"
    const val MEASUREMENTS = "measurements"
    const val ADD_MEASUREMENT = "add_measurement"
    const val ADD_MEASUREMENT_WITH_CALC = "add_measurement/{calcValue}/{calcUnit}"
    const val MEASUREMENT_DETAIL = "measurement_detail/{id}"
    const val PROJECTS = "projects"
    const val ADD_PROJECT = "add_project"
    const val PROJECT_DETAIL = "project_detail/{id}"
    const val CALCULATORS = "calculators"
    const val CONVERTER = "converter"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DASHBOARD, "Dashboard", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Routes.MEASUREMENTS, "Measure", Icons.Filled.Straighten, Icons.Outlined.Straighten),
    BottomNavItem(Routes.PROJECTS, "Projects", Icons.Filled.Folder, Icons.Outlined.FolderOpen),
    BottomNavItem(Routes.CALCULATORS, "Calc", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    BottomNavItem(Routes.CONVERTER, "Convert", Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz)
)

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    val measurementsViewModel: MeasurementsViewModel = viewModel()
    val projectsViewModel: ProjectsViewModel = viewModel()
    val context = LocalContext.current
    val app = context.applicationContext as BuildStackApplication
    val scope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn() + slideInHorizontally { it / 4 } },
        exitTransition = { fadeOut() + slideOutHorizontally { -it / 4 } },
        popEnterTransition = { fadeIn() + slideInHorizontally { -it / 4 } },
        popExitTransition = { fadeOut() + slideOutHorizontally { it / 4 } }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(Routes.ONBOARDING) {
                    popUpTo(Routes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onFinished = {
                scope.launch { app.userPreferences.setHasSeenOnboarding(true) }
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.ONBOARDING) { inclusive = true }
                }
            })
        }

        composable(Routes.MAIN) {
            MainScreen(
                measurementsViewModel = measurementsViewModel,
                projectsViewModel = projectsViewModel,
                onNavigateToMeasurementDetail = { id ->
                    navController.navigate("measurement_detail/$id")
                },
                onNavigateToAddMeasurement = { calcValue, calcUnit ->
                    if (calcValue != null && calcUnit != null) {
                        val encodedValue = Uri.encode(calcValue)
                        val encodedUnit = Uri.encode(calcUnit)
                        navController.navigate("add_measurement_prefill/$encodedValue/$encodedUnit")
                    } else {
                        navController.navigate(Routes.ADD_MEASUREMENT)
                    }
                },
                onNavigateToProjectDetail = { id ->
                    navController.navigate("project_detail/$id")
                },
                onNavigateToAddProject = {
                    navController.navigate(Routes.ADD_PROJECT)
                },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = "measurement_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            MeasurementDetailScreen(
                measurementId = id,
                viewModel = measurementsViewModel,
                projectsViewModel = projectsViewModel,
                onBack = { navController.popBackStack() },
                onAddToCalculator = { value, unit ->
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ADD_MEASUREMENT) {
            AddMeasurementScreen(
                viewModel = measurementsViewModel,
                projectsViewModel = projectsViewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = "add_measurement_prefill/{calcValue}/{calcUnit}",
            arguments = listOf(
                navArgument("calcValue") { type = NavType.StringType },
                navArgument("calcUnit") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val calcValue = backStackEntry.arguments?.getString("calcValue")
            val calcUnit = backStackEntry.arguments?.getString("calcUnit")
            AddMeasurementScreen(
                viewModel = measurementsViewModel,
                projectsViewModel = projectsViewModel,
                prefillValue = calcValue,
                prefillUnit = calcUnit,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(Routes.ADD_PROJECT) {
            AddProjectScreen(
                viewModel = projectsViewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = "project_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            ProjectDetailScreen(
                projectId = id,
                viewModel = projectsViewModel,
                measurementsViewModel = measurementsViewModel,
                onBack = { navController.popBackStack() },
                onNavigateToMeasurement = { mId -> navController.navigate("measurement_detail/$mId") },
                onAddMeasurement = { navController.navigate(Routes.ADD_MEASUREMENT) }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun MainScreen(
    measurementsViewModel: MeasurementsViewModel,
    projectsViewModel: ProjectsViewModel,
    onNavigateToMeasurementDetail: (Long) -> Unit,
    onNavigateToAddMeasurement: (String?, String?) -> Unit,
    onNavigateToProjectDetail: (Long) -> Unit,
    onNavigateToAddProject: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            QuantiaBottomNavBar(
                currentRoute = currentRoute,
                navController = innerNavController
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    onNavigateToMeasurements = {
                        innerNavController.navigate(Routes.MEASUREMENTS) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToProjects = {
                        innerNavController.navigate(Routes.PROJECTS) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToCalculators = {
                        innerNavController.navigate(Routes.CALCULATORS) {
                            launchSingleTop = true
                        }
                    },
                    onNavigateToConverter = {
                        innerNavController.navigate(Routes.CONVERTER) {
                            launchSingleTop = true
                        }
                    },
                    onMeasurementClick = onNavigateToMeasurementDetail,
                    onProjectClick = onNavigateToProjectDetail,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToSettings = onNavigateToSettings
                )
            }

            composable(Routes.MEASUREMENTS) {
                MeasurementsScreen(
                    viewModel = measurementsViewModel,
                    projectsViewModel = projectsViewModel,
                    onMeasurementClick = onNavigateToMeasurementDetail,
                    onAddMeasurement = { onNavigateToAddMeasurement(null, null) }
                )
            }

            composable(Routes.PROJECTS) {
                ProjectsScreen(
                    viewModel = projectsViewModel,
                    measurementsViewModel = measurementsViewModel,
                    onProjectClick = onNavigateToProjectDetail,
                    onAddProject = onNavigateToAddProject
                )
            }

            composable(Routes.CALCULATORS) {
                CalculatorsScreen(
                    onSaveToMeasurements = { value, unit ->
                        onNavigateToAddMeasurement(value, unit)
                    }
                )
            }

            composable(Routes.CONVERTER) {
                ConverterScreen()
            }
        }
    }
}

@Composable
fun QuantiaBottomNavBar(
    currentRoute: String?,
    navController: NavController
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.DASHBOARD) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
            )
        }
    }
}
