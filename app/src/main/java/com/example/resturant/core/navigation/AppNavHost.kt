package com.example.resturant.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.resturant.core.settings.SettingsViewModel
import com.example.resturant.feature.account.AccountScreen
import com.example.resturant.feature.attendance.AttendanceScreen
import com.example.resturant.feature.employee.EmployeeScreen
import com.example.resturant.feature.finance.FinanceScreen
import com.example.resturant.feature.login.LoginScreen
import com.example.resturant.feature.mainpage.MainPage
import com.example.resturant.feature.note.NoteScreen
import com.example.resturant.feature.onboarding.OnboardingScreen
import com.example.resturant.feature.payroll.PayrollScreen
import com.example.resturant.feature.product.ProductScreen
import com.example.resturant.feature.setup.SetupScreen
import com.example.resturant.feature.splash.SplashScreen
import kotlinx.coroutines.delay

@Composable
fun AppNavHost(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val onboardingDone by settingsViewModel.onboardingDone.collectAsState()
    val setupDone by settingsViewModel.setupDone.collectAsState()
    val restaurantName by settingsViewModel.restaurantName.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            LaunchedEffect(onboardingDone, setupDone) {
                delay(1800)
                var waited = 0
                while ((onboardingDone == null || setupDone == null) && waited < 3000) {
                    delay(100)
                    waited += 100
                }
                val dest = when {
                    onboardingDone != true -> Screen.Onboarding.route
                    setupDone != true -> Screen.Setup.route
                    else -> Screen.Login.route
                }
                navController.navigate(dest) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
            SplashScreen(onTimeout = { })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    settingsViewModel.setOnboardingDone()
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Setup.route) {
            SetupScreen(
                onDone = { name, pin ->
                    settingsViewModel.completeSetup(name, pin)
                    navController.navigate(Screen.MainPage.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                settingsViewModel = settingsViewModel,
                onUnlock = {
                    navController.navigate(Screen.MainPage.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MainPage.route) {
            MainPage(
                restaurantName = restaurantName,
                onFinanceClick = { navController.navigate(Screen.Finance.route) },
                onEmployeeClick = { navController.navigate(Screen.Employee.route) },
                onNoteClick = { navController.navigate(Screen.Note.route) },
                onAttendanceClick = { navController.navigate(Screen.Attendance.route) },
                onProductsClick = { navController.navigate(Screen.Products.route) },
                onPayrollClick = { navController.navigate(Screen.Payroll.route) },
                onAccountClick = { navController.navigate(Screen.Account.route) }
            )
        }

        composable(Screen.Account.route) {
            AccountScreen(
                settingsViewModel = settingsViewModel,
                onBack = { navController.popBackStack() },
                onLock = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.MainPage.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Finance.route) {
            FinanceScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Employee.route) {
            EmployeeScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Note.route) {
            NoteScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Attendance.route) {
            AttendanceScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Products.route) {
            ProductScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Payroll.route) {
            PayrollScreen(onBack = { navController.popBackStack() })
        }
    }
}