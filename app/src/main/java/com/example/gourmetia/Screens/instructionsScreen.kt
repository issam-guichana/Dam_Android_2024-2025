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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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

    LaunchedEffect(ingredients) {
        Log.d("RecipeGenerationScreen", "Received ingredients: ${ingredients.size}")
        ingredients.forEach {
            Log.d("RecipeGenerationScreen", "Ingredient: ${it.name}, Quantity: ${it.quantity}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF6A8B9),  // Light pink
                        Color(0xFFFCE0E2)   // Light pink
                    )
                )
            )
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Recipe Generation", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFF597B)
                    )
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo and Title in a white rounded card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
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
                                fontSize = 24.sp,
                                color = Color(0xFFFF597B)
                            )
                        )
                    }
                }

                // Ingredients Preview Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ingredients Detected:",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF597B)
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ingredients.forEach { ingredient ->
                            Text(
                                text = "• ${ingredient.name}: ${ingredient.quantity}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }

                // Number of Persons Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Number of Persons",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color(0xFFFF597B),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("1", "2", "3", "4", "5", "6").forEach { num ->
                                Button(
                                    onClick = { numberOfPersons = num },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (numberOfPersons == num)
                                            Color(0xFFFF597B) else Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    Text(num)
                                }
                            }
                        }
                    }
                }

                // Cuisine Selection Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Select Cuisine",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color(0xFFFF597B),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            cuisineOptions.forEach { cuisine ->
                                Button(
                                    onClick = { selectedCuisine = cuisine },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (selectedCuisine == cuisine)
                                            Color(0xFFFF597B) else Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(cuisine)
                                }
                            }
                        }
                    }
                }

                // Meal Type Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Meal Type",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = Color(0xFFFF597B),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            mealTypeOptions.forEach { type ->
                                Button(
                                    onClick = { mealType = type },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (mealType == type)
                                            Color(0xFFFF597B) else Color.Gray.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(type)
                                }
                            }
                        }
                    }
                }

                // Dietary Restrictions TextField
                OutlinedTextField(
                    value = dietaryRestrictions,
                    onValueChange = { dietaryRestrictions = it },
                    label = { Text("Any Dietary Restrictions?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFFF597B),
                        focusedBorderColor = Color(0xFFFF597B),
                        unfocusedLabelColor = Color(0xFFFF597B),
                        focusedLabelColor = Color(0xFFFF597B)
                    )
                )

                // Generate Recipe Button
                Button(
                    onClick = {
                        if (ingredients.isNotEmpty() && selectedCuisine.isNotBlank() && mealType.isNotBlank()) {
                            val recipeParams = RecipeParams(
                                ingredients = ingredients,
                                numberOfPersons = numberOfPersons,
                                cuisine = selectedCuisine,
                                dietaryRestrictions = dietaryRestrictions,
                                mealType = mealType
                            )
                            val encodedParams = java.net.URLEncoder.encode(
                                Json.encodeToString(recipeParams),
                                "UTF-8"
                            )
                            navController.navigate("recipe_result/$encodedParams")
                        } else {
                            Log.d("RecipeGeneration", "Please fill all required fields")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(vertical = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFFF597B),
                                        Color(0xFFFF8BA0)
                                    )
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = "Generate Recipe")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Recipe")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
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