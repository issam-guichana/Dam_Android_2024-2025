package com.example.gourmetia.Screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gourmetia.Navigation.Screen
import com.example.gourmetia.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeGenerationScreen(
    navController: NavController,
    ingredients: List<Ingredient> = emptyList()
) {
    var numberOfPersons by remember { mutableStateOf("2") }
    var selectedCuisine by remember { mutableStateOf("") }
    var dietaryRestrictions by remember { mutableStateOf("") }
    var mealType by remember { mutableStateOf("") }

    val cuisineOptions = listOf(
        "Italian", "French", "Mexican",
        "Japanese", "Indian", "Mediterranean",
        "Tunisian", "Chinese", "Thai"
    )

    val mealTypeOptions = listOf(
        "Breakfast", "Lunch", "Dinner",
        "Appetizer", "Main Course", "Dessert"
    )

    // Optional: Add a way to set ingredients if they weren't passed initially
    LaunchedEffect(ingredients) {
        Log.d("RecipeGenerationScreen", "Received ingredients: ${ingredients.size}")
        ingredients.forEach {
            Log.d("RecipeGenerationScreen", "Ingredient: ${it.name}, Quantity: ${it.quantity}")
        }
    }
    Scaffold(
        containerColor = Color(0xFFF0F4F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.gourmetia_logo),
                    contentDescription = "Gourmet AI Logo",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Recipe Generation",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
            }

            // Ingredients Preview Card
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
                        text = "Ingredients Detected:",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ingredients.forEach { ingredient ->
                        Text(
                            text = "• ${ingredient.name}: ${ingredient.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Number of Persons
            Text(
                text = "Number of Persons",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                listOf("1", "2", "3", "4", "5", "6").forEach { num ->
                    Button(
                        onClick = { numberOfPersons = num },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (numberOfPersons == num) Color(0xFF2ECC71) else Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(num)
                    }
                }
            }

            // Cuisine Selection
            Text(
                text = "Select Cuisine",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                cuisineOptions.forEach { cuisine ->
                    Button(
                        onClick = { selectedCuisine = cuisine },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedCuisine == cuisine) Color(0xFF2ECC71) else Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(cuisine)
                    }
                }
            }

            // Meal Type Selection
            Text(
                text = "Meal Type",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                mealTypeOptions.forEach { type ->
                    Button(
                        onClick = { mealType = type },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (mealType == type) Color(0xFF2ECC71) else Color.Gray.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(type)
                    }
                }
            }

            // Dietary Restrictions
            TextField(
                value = dietaryRestrictions,
                onValueChange = { dietaryRestrictions = it },
                label = { Text("Any Dietary Restrictions?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFF2ECC71).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    cursorColor = Color(0xFF2ECC71),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Generate Recipe Button
            Button(
                onClick = {
                    // Validate inputs
                    if (ingredients.isNotEmpty() && selectedCuisine.isNotBlank() && mealType.isNotBlank()) {
                        // Encode all parameters to pass to the next screen
                        val recipeParams = RecipeParams(
                            ingredients = ingredients,
                            numberOfPersons = numberOfPersons,
                            cuisine = selectedCuisine,
                            dietaryRestrictions = dietaryRestrictions,
                            mealType = mealType
                        )

                        // Encode the parameters to JSON
                        val encodedParams = java.net.URLEncoder.encode(
                            Json.encodeToString(recipeParams),
                            "UTF-8"
                        )

                        // Navigate to the Recipe Result Screen
                        navController.navigate("recipe_result/$encodedParams")
                    } else {
                        // Show an error or prevent navigation
                        Log.d("RecipeGeneration", "Please fill all required fields")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2ECC71)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = "Generate Recipe")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Recipe")
            }
        }
    }
}

// Data class to hold all recipe generation parameters
@Serializable
data class RecipeParams(
    val ingredients: List<Ingredient>,
    val numberOfPersons: String,
    val cuisine: String,
    val dietaryRestrictions: String,
    val mealType: String
)

@Preview(showBackground = true)
@Composable
fun RecipeGenerationScreenPreview() {
    val sampleIngredients = listOf(
        Ingredient("Chicken", "500g"),
        Ingredient("Tomatoes", "3 pieces"),
        Ingredient("Onions", "2 pieces")
    )
    RecipeGenerationScreen(
        navController = rememberNavController(),
        ingredients = sampleIngredients
    )
}