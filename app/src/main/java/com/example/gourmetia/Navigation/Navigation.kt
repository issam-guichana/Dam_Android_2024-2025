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
import com.example.gourmetia.Screens.Ingredient
import com.example.gourmetia.Screens.IngredientIdentificationScreen
import com.example.gourmetia.Screens.RecipeGenerationScreen
import com.example.gourmetia.Screens.RecipeParams
import com.example.gourmetia.Screens.RecipeResultScreen
import com.example.gourmetia.SignUpScreen
import com.example.gourmetia.ViewModels.AuthViewModel
import kotlinx.serialization.json.Json

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object SignUp : Screen("signup")
    object ForgotPassword : Screen("forgot_password")  // This is using underscore
    object Home : Screen("home")
    object OTPVerf : Screen("OTPVerf/{email}")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object GeminiChat : Screen("gemini_chat")
    object RecipeGeneration : Screen("recipe_generation/{ingredientsList}")
    object RecipeResult : Screen("recipe_result/{recipeParams}")
    companion object {
        fun fromRoute(route: String?): Screen {
            Log.d("Navigation", "Attempting to navigate to route: $route")
            return when (route) {
                "home" -> Home
                "signup" -> SignUp
                "forgot_password" -> ForgotPassword  // Fixed to match the route
                "OTPVerf" -> OTPVerf  // Fixed to match the route
                "profile" -> Profile
                "gemini_chat" -> GeminiChat
                "recipe_result" -> RecipeResult
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
        composable(Screen.GeminiChat.route) {
            Log.d("Navigation", "Composing Ingredient Identification Screen")
            IngredientIdentificationScreen(navController)
        }
        composable(
            route = Screen.RecipeGeneration.route,
            arguments = listOf(
                navArgument("ingredientsList") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val ingredientsJson = backStackEntry.arguments?.getString("ingredientsList") ?: ""
            val ingredients = try {
                if (ingredientsJson.isNotBlank()) {
                    val decodedJson = java.net.URLDecoder.decode(ingredientsJson, "UTF-8")
                    Json.decodeFromString<List<Ingredient>>(decodedJson)
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("RecipeGeneration", "Error parsing ingredients JSON: ${e.localizedMessage}")
                emptyList()
            }
            RecipeGenerationScreen(navController,ingredients = ingredients)
        }
        composable(
            route = Screen.RecipeResult.route,
            arguments = listOf(
                navArgument("recipeParams") {
                    type = NavType.StringType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val recipeParamsJson = backStackEntry.arguments?.getString("recipeParams") ?: ""
            val recipeParams = try {
                val decodedJson = java.net.URLDecoder.decode(recipeParamsJson, "UTF-8")
                Json.decodeFromString<RecipeParams>(decodedJson)
            } catch (e: Exception) {
                Log.e("RecipeResult", "Error parsing recipe params JSON: ${e.localizedMessage}")
                null
            }

            recipeParams?.let { params ->
                RecipeResultScreen(
                    navController,
                    ingredients = params.ingredients,
                    numberOfPersons = params.numberOfPersons,
                    selectedCuisine = params.cuisine,
                    dietaryRestrictions = params.dietaryRestrictions,
                    mealType = params.mealType
                )
            }
        }
    }
}