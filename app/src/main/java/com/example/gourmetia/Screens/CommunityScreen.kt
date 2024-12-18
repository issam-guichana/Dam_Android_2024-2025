package com.example.gourmetia.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Community Recipes",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA),
                    titleContentColor = Color(0xFF2C3E50)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF1F3F5))
        ) {
            items(CommunityPostRepository.dummyPosts) { post ->
                CommunityPostCard(post)
            }
        }
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost) {
    var likes by remember { mutableStateOf(post.likes) }
    var dislikes by remember { mutableStateOf(post.dislikes) }
    var hasLiked by remember { mutableStateOf(false) }
    var hasDisliked by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // User Info Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(post.userProfileImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFAEB6BF),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E50)
                        )
                    )
                    Text(
                        text = "Community Chef",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7F8C8D)
                        )
                    )
                }
            }

            // Recipe Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(post.recipeImage)
                    .crossfade(true)
                    .build(),
                contentDescription = post.recipeTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )

            // Post Details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = post.recipeTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF34495E)
                    )
                )
            }

            // Interaction Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                InteractionButton(
                    icon = Icons.Filled.ThumbUp,
                    count = likes,
                    isSelected = hasLiked,
                    selectedColor = Color(0xFF3498DB),
                    onClick = {
                        if (!hasLiked) {
                            likes++
                            if (hasDisliked) {
                                dislikes--
                                hasDisliked = false
                            }
                            hasLiked = true
                        } else {
                            likes--
                            hasLiked = false
                        }
                    }
                )

                // Dislike Button
                InteractionButton(
                    icon = Icons.Filled.ThumbDown,
                    count = dislikes,
                    isSelected = hasDisliked,
                    selectedColor = Color(0xFFE74C3C),
                    onClick = {
                        if (!hasDisliked) {
                            dislikes++
                            if (hasLiked) {
                                likes--
                                hasLiked = false
                            }
                            hasDisliked = true
                        } else {
                            dislikes--
                            hasDisliked = false
                        }
                    }
                )

                // Favorite Button
                InteractionButton(
                    icon = Icons.Filled.FavoriteBorder,
                    count = 0,
                    isSelected = false,
                    selectedColor = Color(0xFFE74C3C),
                    onClick = { /* Favorite action */ }
                )

                // Share Button
                IconButton(
                    onClick = { /* Share action */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F3F5))
                ) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color(0xFF2C3E50)
                    )
                }
            }
        }
    }
}

@Composable
fun InteractionButton(
    icon: ImageVector,
    count: Int,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) selectedColor.copy(alpha = 0.1f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) selectedColor else Color(0xFF7F8C8D),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = count.toString(),
            color = if (isSelected) selectedColor else Color(0xFF7F8C8D),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}