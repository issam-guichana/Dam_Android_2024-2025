package com.example.gourmetia.Screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gourmetia.R
import com.example.gourmetia.ViewModels.AuthViewModel
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RecipeDetails(
    val id: String? = null, // Add this line
    val name: String = "",
    val ingredients: List<Ingredient> = emptyList(),
    val instructions: List<String> = emptyList(),
    val cookingTime: String = "",
    val servings: Int = 2,
    val nutrients: Map<String, String> = emptyMap(),
    val imageUrl: String = "" // Add this for image representation
)

@Composable
fun RecipeResultScreen(
    navController: NavController,
    ingredients: List<Ingredient>,
    numberOfPersons: String,
    selectedCuisine: String,
    dietaryRestrictions: String,
    mealType: String
) {
    var recipe by remember { mutableStateOf<RecipeDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = ingredients) {
        try {
            recipe = generateRecipe(
                ingredients = ingredients,
                numberOfPersons = numberOfPersons,
                cuisine = selectedCuisine,
                dietaryRestrictions = dietaryRestrictions,
                mealType = mealType
            )
            isLoading = false
        } catch (e: Exception) {
            Log.e("RecipeGeneration", "Detailed error", e)
            errorMessage = "Failed to generate recipe: ${e.message}"
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color(0xFFF0F4F8)
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFF2ECC71))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Generating your recipe...", color = Color(0xFF2ECC71))
                }
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            recipe?.let { recipeDetails ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Recipe Title
                    Text(
                        text = recipeDetails.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Recipe Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFF3E0)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Recipe Details",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text("Servings: ${recipeDetails.servings}")
                            Text("Cooking Time: ${recipeDetails.cookingTime}")
                        }
                    }

                    // Ingredients
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    recipeDetails.ingredients.forEach { ingredient ->
                        Text(
                            text = "• ${ingredient.name}: ${ingredient.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // Instructions
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    recipeDetails.instructions.forEachIndexed { index, instruction ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF2ECC71),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "${index + 1}. $instruction",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // Nutritional Information
                    if (recipeDetails.nutrients.isNotEmpty()) {
                        Text(
                            text = "Nutritional Information",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        recipeDetails.nutrients.forEach { (nutrient, value) ->
                            Text(
                                text = "$nutrient: $value",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    // New Navigation Buttons Section
                    Spacer(modifier = Modifier.height(16.dp))
                    NavigationButtons(
                        navController = navController,
                        recipeDetails = recipe!!, // Pass the generated recipe
                        ingredients = ingredients,
                        numberOfPersons = numberOfPersons,
                        selectedCuisine = selectedCuisine,
                        dietaryRestrictions = dietaryRestrictions,
                        mealType = mealType,
                        authViewModel = viewModel() // Assuming you're using a ViewModel
                    )
                }
            }
        }
    }
}

suspend fun generateRecipe(
    ingredients: List<Ingredient>,
    numberOfPersons: String,
    cuisine: String,
    dietaryRestrictions: String,
    mealType: String
): RecipeDetails = withContext(Dispatchers.IO) {
    try {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyB-lwjXMpc6O-pb7ZYSkXpNynowfQLwKKU"
        )

        val ingredientsList = ingredients.joinToString(", ") { "${it.name} (${it.quantity})" }

        val prompt = """
            Generate a ${cuisine} ${mealType} recipe that perfectly incorporates these ingredients: $ingredientsList
            
            Requirements:
            - Serves: $numberOfPersons people
            - Consider the following dietary restrictions: $dietaryRestrictions
            
            Please provide the recipe details in the following structured format:
            
            Recipe Name:
            Servings:
            Cooking Time:
            
            Ingredients:
            - (list with quantities for $numberOfPersons people)
            
            Instructions:
            1. Step One
            2. Step Two
            ...
            
            Nutritional Information:
            - Calories per serving
            - Protein
            - Carbohydrates
            - Fat
        """.trimIndent()

        if (ingredients.isEmpty()) {
            throw IllegalArgumentException("No ingredients provided")
        }

        val response = generativeModel.generateContent(prompt)

        // Parse the response
        val recipeText = response.text ?: throw Exception("No recipe generated")
        parseRecipeResponse(recipeText)
    } catch (e: Exception) {
        Log.e("RecipeGeneration", "Error generating recipe", e)
        throw e
    }
}

fun parseRecipeResponse(responseText: String): RecipeDetails {
    val lines = responseText.split("\n")

    val name = lines.find { it.startsWith("Recipe Name:") }
        ?.substringAfter("Recipe Name:")?.trim() ?: "Unnamed Recipe"

    val servings = lines.find { it.startsWith("Servings:") }
        ?.substringAfter("Servings:")?.trim()?.toIntOrNull() ?: 2

    val cookingTime = lines.find { it.startsWith("Cooking Time:") }
        ?.substringAfter("Cooking Time:")?.trim() ?: "Not specified"

    val ingredientsStart = lines.indexOfFirst { it.startsWith("Ingredients:") }
    val instructionsStart = lines.indexOfFirst { it.startsWith("Instructions:") }
    val nutritionStart = lines.indexOfFirst { it.startsWith("Nutritional Information:") }

    val ingredients = if (ingredientsStart != -1 && instructionsStart != -1) {
        lines.slice(ingredientsStart + 1 until instructionsStart)
            .filter { it.startsWith("- ") }
            .map { line ->
                val parts = line.substring(2).split("(", ")")
                Ingredient(
                    name = parts[0].trim(),
                    quantity = parts.getOrNull(1)?.trim() ?: "Not specified"
                )
            }
    } else emptyList()

    val instructions = if (instructionsStart != -1 && (nutritionStart == -1 || instructionsStart < nutritionStart)) {
        lines.slice(instructionsStart + 1 until (nutritionStart.takeIf { it != -1 } ?: lines.size))
            .filter { it.matches(Regex("\\d+\\..*")) }
            .map { it.substringAfter(". ").trim() }
    } else emptyList()

    val nutrients = if (nutritionStart != -1) {
        lines.slice(nutritionStart + 1 until lines.size)
            .filter { it.contains(":") }
            .associate { line ->
                val (key, value) = line.split(":")
                key.trim() to value.trim()
            }
    } else emptyMap()

    return RecipeDetails(
        name = name,
        ingredients = ingredients,
        instructions = instructions,
        servings = servings,
        cookingTime = cookingTime,
        nutrients = nutrients
    )
}


@Composable
fun NavigationButtons(
    navController: NavController,
    recipeDetails: RecipeDetails, // Add this parameter
    ingredients: List<Ingredient>,
    numberOfPersons: String,
    selectedCuisine: String,
    dietaryRestrictions: String,
    mealType: String,
    authViewModel: AuthViewModel // Add AuthViewModel to access user context
) {
    val context = LocalContext.current
    // Define button configurations
    val navigationButtons = listOf(
        NavigationButtonConfig(
            icon = Icons.Default.Refresh,
            text = "Another Recipe",
            onClick = {
                // Navigate back to ingredient selection to generate a new recipe
                navController.navigate("ingredient_selection") {
                    popUpTo("recipe_result") { inclusive = true }
                }
            }
        ),
        NavigationButtonConfig(
            icon = Icons.Default.Home,
            text = "Home",
            onClick = {
                // Navigate to home screen
                navController.navigate("home") {
                    popUpTo("recipe_result") { inclusive = true }
                }
            }
        ),
        NavigationButtonConfig(
            icon = Icons.Default.FavoriteBorder,
            text = "Add to Favorites",
            onClick = {
                val userId = authViewModel.getUserId(context)

                if (userId != null) {
                    // Convert RecipeDetails to FavouriteRecipe
                    val favouriteRecipe = FavouriteRecipe(
                        id = recipeDetails.id ?: UUID.randomUUID().toString(),
                        title = recipeDetails.name,
                        imageUrl = recipeDetails.imageUrl, // Ensure this is populated
                        cookTime = recipeDetails.cookingTime,
                        difficulty = determineDifficulty(recipeDetails)
                    )

                    // Call ViewModel method to toggle bookmark
                    authViewModel.toggleBookmark(
                        context = context,
                        recipeId = favouriteRecipe.id,
                        recipe = favouriteRecipe,
                        onSuccess = {
                            // Show success message or update UI
                            Toast.makeText(context, "Recipe added to favorites!", Toast.LENGTH_SHORT).show()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    // Handle case where user is not logged in
                    Toast.makeText(context, "Please log in to save favorites", Toast.LENGTH_SHORT).show()
                }
            }
        ),
        NavigationButtonConfig(
            icon = Icons.Default.List,
            text = "Recipes List",
            onClick = {
                // Navigate to saved recipes list
                navController.navigate("recipe_list")
            }
        )
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            navigationButtons.forEach { buttonConfig ->
                NavigationButton(
                    icon = buttonConfig.icon,
                    text = buttonConfig.text,
                    onClick = buttonConfig.onClick
                )
            }
        }
    }
}

data class NavigationButtonConfig(
    val icon: ImageVector,
    val text: String,
    val onClick:  () -> Unit
)

@Composable
fun NavigationButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(56.dp)
                .padding(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF2ECC71)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF2ECC71)
        )
    }
}
fun determineDifficulty(recipeDetails: RecipeDetails): String {
    return when {
        recipeDetails.instructions.size <= 3 -> "Easy"
        recipeDetails.instructions.size <= 6 -> "Medium"
        else -> "Hard"
    }
}


@Preview(showBackground = true)
@Composable
fun RecipeResultScreenPreview() {
    val sampleIngredients = listOf(
        Ingredient("Chicken", "500g"),
        Ingredient("Tomatoes", "3 pieces"),
        Ingredient("Onions", "2 pieces")
    )
    // Note: In a preview, you'd mock a NavController or use a dummy implementation
    // RecipeResultScreen(
    //     navController = mockNavController,
    //     ingredients = sampleIngredients,
    //     numberOfPersons = "2",
    //     selectedCuisine = "Italian",
    //     dietaryRestrictions = "None",
    //     mealType = "Dinner"
    // )
}