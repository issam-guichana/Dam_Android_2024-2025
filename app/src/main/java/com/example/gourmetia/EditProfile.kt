package com.example.gourmetia

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gourmetia.ViewModels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf<String?>(null) }

    // Load saved user data
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        name = prefs.getString("user_name", "") ?: ""
        email = prefs.getString("user_email", "") ?: ""
        phone = prefs.getString("user_phone", "") ?: "+216 25 786 329"

        authViewModel.getUserById(
            context = context,
            onSuccess = { response ->
                name = response.user.name
                email = response.user.email
            },
            onError = { error ->
                showError = error
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF597B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFFFF597B)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            authViewModel.updateProfile(
                                context = context,
                                name = name,
                                email = email,
                                onSuccess = {
                                    navController.navigateUp()
                                },
                                onError = { error ->
                                    showError = error
                                }
                            )
                        },
                        enabled = !authViewModel.isLoading.value
                    ) {
                        if (authViewModel.isLoading.value) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFFFF597B)
                            )
                        } else {
                            Text(
                                "Save",
                                color = Color(0xFFFF597B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF6A8B9),
                            Color(0xFFFCE0E2)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    color = Color.White
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .padding(24.dp)
                            .size(72.dp),
                        tint = Color(0xFFFF597B)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = "Name",
                            tint = Color(0xFFFF597B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFFF597B),
                        focusedBorderColor = Color(0xFFFF597B),
                        unfocusedLabelColor = Color(0xFFFF597B),
                        focusedLabelColor = Color(0xFFFF597B)
                    )
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Email,
                            contentDescription = "Email",
                            tint = Color(0xFFFF597B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFFF597B),
                        focusedBorderColor = Color(0xFFFF597B),
                        unfocusedLabelColor = Color(0xFFFF597B),
                        focusedLabelColor = Color(0xFFFF597B)
                    )
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Phone,
                            contentDescription = "Phone",
                            tint = Color(0xFFFF597B)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFFF597B),
                        focusedBorderColor = Color(0xFFFF597B),
                        unfocusedLabelColor = Color(0xFFFF597B),
                        focusedLabelColor = Color(0xFFFF597B)
                    )
                )

                Button(
                    onClick = {
                        authViewModel.updateProfile(
                            context = context,
                            name = name,
                            email = email,
                            onSuccess = {
                                navController.navigateUp()
                            },
                            onError = { error ->
                                showError = error
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    enabled = !authViewModel.isLoading.value
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
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (authViewModel.isLoading.value) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Save Changes",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    // Error dialog
    showError?.let { error ->
        AlertDialog(
            onDismissRequest = { showError = null },
            title = { Text("Error") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = { showError = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(navController = rememberNavController())
}