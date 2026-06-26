package com.example.resturant.core.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Setup : Screen("setup")
    data object Login : Screen("login")
    data object MainPage : Screen("main_page")
    data object Finance : Screen("finance")
    data object Employee : Screen("employee")
    data object Note : Screen("note")
    data object Attendance : Screen("attendance")
    data object Products : Screen("products")
    data object Payroll : Screen("payroll")
    data object Account : Screen("account")
}