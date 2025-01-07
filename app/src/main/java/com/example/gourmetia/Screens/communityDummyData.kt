package com.example.gourmetia.Screens

import androidx.compose.runtime.Stable

@Stable
data class CommunityPost(
    val id: String,
    val userName: String,
    val recipeTitle: String,
    val description: String,
    val likes: Int,
    val dislikes: Int
)
