package com.example.gourmetia.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

// Data class for Favourite Recipes
data class FavouriteRecipe(
    val id: String,
    val title: String,
    val imageUrl: String?,
    val cookTime: String,
    val difficulty: String
)

// Repository or ViewModel would typically manage this
object FavouriteRecipeRepository {
    val dummyFavouriteRecipes = listOf(
        FavouriteRecipe(
            id = "1",
            title = "Creamy Pasta Carbonara",
            imageUrl = "https://example.com/carbonara.jpg",
            cookTime = "30 mins",
            difficulty = "Medium"
        ),
        FavouriteRecipe(
            id = "2",
            title = "Classic Chocolate Cake",
            imageUrl = "https://example.com/chocolate-cake.jpg",
            cookTime = "1 hour",
            difficulty = "Hard"
        ),
        // Add more dummy recipes
        FavouriteRecipe(
            id = "3",
            title = "Vegetarian Stir Fry",
            imageUrl = "https://example.com/stir-fry.jpg",
            cookTime = "25 mins",
            difficulty = "Easy"
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouritesScreen(navController: NavController) {
    var favouriteRecipes by remember {
        mutableStateOf(FavouriteRecipeRepository.dummyFavouriteRecipes)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Favourites",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // Clear all favourites functionality
                        favouriteRecipes = emptyList()
                    }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Clear All",
                            tint = Color(0xFFE74C3C)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA),
                    titleContentColor = Color(0xFF2C3E50)
                )
            )
        }
    ) { paddingValues ->
        if (favouriteRecipes.isEmpty()) {
            EmptyFavouritesView()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF1F3F5)),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favouriteRecipes) { recipe ->
                    FavouriteRecipeCard(
                        recipe = recipe,
                        onRemove = {
                            favouriteRecipes = favouriteRecipes.filter { it.id != recipe.id }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavouriteRecipeCard(
    recipe: FavouriteRecipe,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column {
                // Recipe Image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(recipe.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp
                            )
                        ),
                    contentScale = ContentScale.Crop
                )

                // Recipe Details
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2C3E50)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = recipe.cookTime,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF7F8C8D)
                            )
                        )
                        Text(
                            text = recipe.difficulty,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = when (recipe.difficulty) {
                                    "Easy" -> Color(0xFF2ECC71)
                                    "Medium" -> Color(0xFFF39C12)
                                    "Hard" -> Color(0xFFE74C3C)
                                    else -> Color(0xFF7F8C8D)
                                }
                            )
                        )
                    }
                }
            }

            // Remove from Favourites Button
            IconButton(
                onClick = onRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        Color.White.copy(alpha = 0.7f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Remove from Favourites",
                    tint = Color(0xFFE74C3C)
                )
            }
        }
    }
}

@Composable
fun EmptyFavouritesView() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F3F5)),
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
                tint = Color(0xFF95A5A6)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Favourite Recipes Yet",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFF7F8C8D)
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