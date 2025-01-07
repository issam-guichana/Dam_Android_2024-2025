package com.example.gourmetia

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gourmetia.Navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    Log.d("HomeScreen", "HomeScreen composable called")

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
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    modifier = Modifier.height(60.dp)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = selectedItem == 0,
                        onClick = { selectedItem = 0 },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF597B),
                            selectedTextColor = Color(0xFFFF597B),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.White
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.CameraEnhance, contentDescription = "AI") },
                        label = { Text("AI") },
                        selected = selectedItem == 1,
                        onClick = {
                            selectedItem = 1
                            navController.navigate(Screen.GeminiChat.route)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF597B),
                            selectedTextColor = Color(0xFFFF597B),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.White
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Favorites") },
                        label = { Text("Favorites") },
                        selected = selectedItem == 2,
                        onClick = {
                            selectedItem = 2
                            navController.navigate(Screen.Favourites.route)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF597B),
                            selectedTextColor = Color(0xFFFF597B),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.White
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.Forum, contentDescription = "community") },
                        label = { Text("Community") },
                        selected = selectedItem == 3,
                        onClick = {
                            selectedItem = 3
                            navController.navigate(Screen.Community.route)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF597B),
                            selectedTextColor = Color(0xFFFF597B),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.White
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = selectedItem == 4,
                        onClick = {
                            selectedItem = 4
                            navController.navigate(Screen.Profile.route)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFFF597B),
                            selectedTextColor = Color(0xFFFF597B),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color.White
                        )
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = WebViewClient()
                                configureWebView()
                                loadUrl("https://www.foodnetwork.com/recipes")
                            }
                        }
                    )
                }
            }
        }
    }
}

fun WebView.configureWebView() {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        loadWithOverviewMode = true
        useWideViewPort = true
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController)
}