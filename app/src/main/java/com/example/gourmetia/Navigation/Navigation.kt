package com.example.gourmetia.Navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gourmetia.AuthScreen
import com.example.gourmetia.EditProfileScreen
import com.example.gourmetia.ForgotPasswordScreen
import com.example.gourmetia.HomeScreen
import com.example.gourmetia.OTPVerificationScreen
import com.example.gourmetia.ProfileScreen
import com.example.gourmetia.SignUpScreen
import com.example.gourmetia.ViewModels.AuthViewModel

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")  // This is using underscore
    object Home : Screen("home")
    object OTPVerf : Screen("OTPVerf/{email}")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")

    companion object {
        fun fromRoute(route: String?): Screen {
            Log.d("Navigation", "Attempting to navigate to route: $route")
            return when (route) {
                "home" -> Home
                "signup" -> SignUp
                "forgot_password" -> ForgotPassword  // Fixed to match the route
                "OTPVerf" -> OTPVerf  // Fixed to match the route
                "profile" -> Profile
                else -> {
                    Log.d("Navigation", "Unknown route: $route, defaulting to Auth")
                    Auth
                }
            }
        }
    }
}

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()
    val authViewModel = AuthViewModel()
    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route

    ) {
        composable(Screen.Auth.route) {
            Log.d("Navigation", "Composing Auth Screen")
            AuthScreen(navController)
        }

        composable(Screen.SignUp.route) {
            Log.d("Navigation", "Composing SignUp Screen")
            SignUpScreen(navController)
        }

        composable(Screen.ForgotPassword.route) {
            Log.d("Navigation", "Composing ForgotPassword Screen")
            ForgotPasswordScreen(navController)
        }

        composable(Screen.Home.route) {
            Log.d("Navigation", "Composing Home Screen")
            HomeScreen(navController)
        }
        composable(
            route = Screen.OTPVerf.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            Log.d("Navigation", "Composing OTPVerification Screen with email: $email")
            OTPVerificationScreen(navController, email, authViewModel)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController)
        }
    }
}