package com.example.gourmetia

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gourmetia.Navigation.Screen
import com.example.gourmetia.ViewModels.AuthViewModel

@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel = AuthViewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isLoading = authViewModel.isLoading.value
    val loginResponse = authViewModel.loginResponse.value

    LaunchedEffect(loginResponse) {
        loginResponse?.let {
            Log.d("AuthScreen", "Login Response: $it")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (it.contains("Success")) {
                Log.d("AuthScreen", "Attempting navigation to Home")
                try {
                    // After successful login, you can get the stored values
                    val accessToken = authViewModel.getAccessToken(context)
                    val userId = authViewModel.getUserId(context)
                    Log.d("AuthScreen", "AccessToken: $accessToken, UserId: $userId")

                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                    Log.d("AuthScreen", "Navigation command executed")
                } catch (e: Exception) {
                    Log.e("AuthScreen", "Navigation failed", e)
                    Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val gradientColors = listOf(
        Color(0xFFFFFBF5),  // Warm white
        Color(0xFFFFD0D0)   // Soft cream
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.gourmetia_logo),
                contentDescription = "Gourmetia Logo",
                modifier = Modifier
                    .size(140.dp)
                    .padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Sign in to continue",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Email,
                                contentDescription = null,
                                tint = Color(0xFFFF5555)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color(0xFFFF5555)
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp)
                    )

                    TextButton(
                        onClick = { navController.navigate(Screen.ForgotPassword.route) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Forgot Password?",
                            color = Color(0xFF1E90FF),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                authViewModel.login(
                                    context = context,  // Add this line
                                    email = email,
                                    password = password,
                                    onSuccess = {
                                        try {
                                            // After successful login, you can get the stored values here too
                                            val accessToken = authViewModel.getAccessToken(context)
                                            val userId = authViewModel.getUserId(context)
                                            Log.d("AuthScreen", "Login Success - AccessToken: $accessToken, UserId: $userId")

                                            navController.navigate("home") {
                                                popUpTo("auth") { inclusive = true }
                                            }
                                        } catch (e: Exception) {
                                            Log.e("AuthScreen", "Navigation failed", e)
                                            Toast.makeText(
                                                context,
                                                "Navigation failed: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    },
                                    onError = { errorMessage ->
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5555)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = { navController.navigate(Screen.SignUp.route) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    "Don't have an account? Sign Up",
                    fontSize = 14.sp,
                    color = Color(0xFF1E90FF)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen(navController = rememberNavController())
}