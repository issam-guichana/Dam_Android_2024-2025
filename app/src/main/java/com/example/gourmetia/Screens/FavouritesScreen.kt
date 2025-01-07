package com.example.gourmetia.Screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.gourmetia.ViewModels.AuthViewModel
import com.example.gourmetia.remote.SharedPrefsUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    var favouriteRecipes by remember { mutableStateOf<List<RecipeDetails>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedRecipe by remember { mutableStateOf<RecipeDetails?>(null) }

    LaunchedEffect(Unit) {
        favouriteRecipes = SharedPrefsUtils.getFavoriteRecipes(context)
        isLoading = false
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
                    title = { Text("My Favourites", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            SharedPrefsUtils.clearAllRecipes(context)
                            favouriteRecipes = emptyList()
                            Toast.makeText(context, "All favorites cleared", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Clear All",
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
        ) { paddingValues ->
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFFF597B))
                    }
                }
                favouriteRecipes.isEmpty() -> {
                    EmptyFavouritesView()
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(
                            top = paddingValues.calculateTopPadding(),
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favouriteRecipes) { recipe ->
                            FavouriteRecipeCard(
                                recipe = recipe,
                                onRemove = {
                                    SharedPrefsUtils.removeRecipe(context, recipe.id!!)
                                    favouriteRecipes = SharedPrefsUtils.getFavoriteRecipes(context)
                                    Toast.makeText(context, "Recipe removed from favorites", Toast.LENGTH_SHORT).show()
                                },
                                onClick = {
                                    selectedRecipe = recipe
                                }
                            )
                        }
                    }

                    selectedRecipe?.let { recipe ->
                        RecipeDetailDialog(
                            recipe = recipe,
                            onDismiss = { selectedRecipe = null }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavouriteRecipeCard(
    recipe: RecipeDetails,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF597B)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                recipe.ingredients.take(3).forEach { ingredient ->
                    Text(
                        text = "• ${ingredient.name}: ${ingredient.quantity}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7F8C8D)
                        )
                    )
                }

                if (recipe.ingredients.size > 3) {
                    Text(
                        text = "... and ${recipe.ingredients.size - 3} more",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7F8C8D)
                        )
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Remove from Favourites",
                    tint = Color(0xFFFF597B)
                )
            }
        }
    }
}

@Composable
fun EmptyFavouritesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Outlined.FavoriteBorder,
                contentDescription = "No Favourites",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFFFF597B)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Favourite Recipes Yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFFFF597B)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start exploring and save your favorite recipes!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF95A5A6)
                )
            )
        }
    }
}

@Composable
fun RecipeDetailDialog(
    recipe: RecipeDetails,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF6A8B9),  // Light pink
                        Color(0xFFFCE0E2)   // Light pink
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        containerColor = Color.Transparent,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Column {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
                Divider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                // Recipe Details Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Recipe Details",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text("Servings: ${recipe.servings}", color = Color.White)
                        Text("Cooking Time: ${recipe.cookingTime}", color = Color.White)
                    }
                }

                // Ingredients Section
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                recipe.ingredients.forEach { ingredient ->
                    Text(
                        text = "• ${ingredient.name}: ${ingredient.quantity}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                // Instructions Section
                Text(
                    text = "Instructions",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                recipe.instructions.forEachIndexed { index, instruction ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "${index + 1}.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            ),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = instruction,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }

                // Nutritional Information Section
                if (recipe.nutrients.isNotEmpty()) {
                    Text(
                        text = "Nutritional Information",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    recipe.nutrients.forEach { (nutrient, value) ->
                        Text(
                            text = "$nutrient: $value",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.White
                )
            ) {
                Text("Close")
            }
        }
    )
}