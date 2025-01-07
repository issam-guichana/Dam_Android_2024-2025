package com.example.gourmetia.Screens

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gourmetia.R
import com.example.gourmetia.ViewModels.AuthViewModel
import com.example.gourmetia.remote.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController,
    viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val posts = viewModel.communityPostsResponse.value
    val isLoading = viewModel.isLoading.value
    val error = viewModel.communityError.value
    val authorDetails = remember { mutableStateMapOf<String, UserData>() }

    LaunchedEffect(key1 = true) {
        if (viewModel.getAccessToken(context) == null) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
            return@LaunchedEffect
        }

        viewModel.getCommunityPosts(
            context = context,
            onSuccess = {
                posts?.forEach { post ->
                    viewModel.getAuthorDetails(
                        authorId = post.author,
                        onSuccess = { userData ->
                            authorDetails[post.author] = userData
                        },
                        onError = { /* Handle error */ }
                    )
                }
            },
            onError = { errorMessage ->
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
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
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.gourmetia_logo),
                                contentDescription = "Gourmet AI Logo",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Community Recipes",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    },
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
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFFFF597B)
                        )
                    }
                    error != null -> {
                        Card(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = error,
                                    color = Color(0xFFFF597B),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.getCommunityPosts(
                                            context = context,
                                            onSuccess = { },
                                            onError = { errorMessage ->
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                            }
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFF597B)
                                    )
                                ) {
                                    Text("Retry")
                                }
                            }
                        }
                    }
                    posts?.isEmpty() == true -> {
                        Card(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "No posts available",
                                modifier = Modifier.padding(16.dp),
                                color = Color(0xFFFF597B)
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(posts ?: emptyList()) { post ->
                                val author = authorDetails[post.author]
                                CommunityPostCard(
                                    CommunityPost(
                                        id = post._id,
                                        userName = author?.name ?: "Issam Guichana",
                                        recipeTitle = "Recipe generated by ${author?.name ?: "Issam"}",
                                        description = post.content,
                                        likes = post.likes.size,
                                        dislikes = post.dislikes.size
                                    )
                                )
                            }
                        }
                    }
                }
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
            .padding(vertical = 8.dp),
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
                    .background(Color.White)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.default_user),  // Add this image to drawable
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = Color(0xFFFF597B),
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
                            color = Color(0xFFFF597B)
                        )
                    )
                    Text(
                        text = "Community Chef",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFFF8BA0)
                        )
                    )
                }
            }

            // Post Details
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = post.recipeTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF597B)
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
                    .background(Color(0xFFFCE0E2))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                InteractionButton(
                    icon = Icons.Filled.ThumbUp,
                    count = likes,
                    isSelected = hasLiked,
                    selectedColor = Color(0xFFFF597B),
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

                InteractionButton(
                    icon = Icons.Filled.ThumbDown,
                    count = dislikes,
                    isSelected = hasDisliked,
                    selectedColor = Color(0xFFFF597B),
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

                InteractionButton(
                    icon = Icons.Filled.FavoriteBorder,
                    count = 0,
                    isSelected = false,
                    selectedColor = Color(0xFFFF597B),
                    onClick = { /* Favorite action */ }
                )

                IconButton(
                    onClick = { /* Share action */ },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = Color(0xFFFF597B)
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
