package com.example.gourmetia.Screens

import androidx.compose.runtime.Stable

@Stable
data class CommunityPost(
    val id: String,
    val userName: String,
    val userProfileImage: String,
    val recipeTitle: String,
    val recipeImage: String,
    val description: String,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

// Dummy data for community posts
object CommunityPostRepository {
    val dummyPosts = listOf(
        CommunityPost(
            id = "1",
            userName = "Chef Maria",
            userProfileImage = "https://randomuser.me/api/portraits/women/1.jpg",
            recipeTitle = "Spicy Chicken Tacos",
            recipeImage = "https://example.com/spicy-tacos.jpg",
            description = "A delicious homemade taco recipe with a spicy kick! Perfect for taco night.",
            likes = 42,
            dislikes = 3
        ),
        CommunityPost(
            id = "2",
            userName = "Pasta Pete",
            userProfileImage = "https://randomuser.me/api/portraits/men/2.jpg",
            recipeTitle = "Creamy Carbonara",
            recipeImage = "https://example.com/carbonara.jpg",
            description = "Classic Italian carbonara with crispy pancetta and a silky egg sauce.",
            likes = 67,
            dislikes = 5
        ),
        CommunityPost(
            id = "3",
            userName = "Baking Betty",
            userProfileImage = "https://randomuser.me/api/portraits/women/3.jpg",
            recipeTitle = "Chocolate Lava Cake",
            recipeImage = "https://example.com/lava-cake.jpg",
            description = "Decadent chocolate lava cake with a molten chocolate center. Easy to make at home!",
            likes = 89,
            dislikes = 2
        )
    )
}