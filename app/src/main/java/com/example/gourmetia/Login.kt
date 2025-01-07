package com.example.gourmetia

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

    // Keep existing LaunchedEffect and response handling
    LaunchedEffect(loginResponse) {
        loginResponse?.let {
            Log.d("AuthScreen", "Login Response: $it")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            if (it.contains("Success")) {
                try {
                    val accessToken = authViewModel.getAccessToken(context)
                    val userId = authViewModel.getUserId(context)
                    Log.d("AuthScreen", "AccessToken: $accessToken, UserId: $userId")
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                } catch (e: Exception) {
                    Log.e("AuthScreen", "Navigation failed", e)
                    Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circular logo container
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .padding(0.dp)
                    .border(
                        width = 6.dp, // Specify the border width
                        color = Color.White, // Specify the border color
                        shape = RoundedCornerShape(50) // Match the shape of the border with the clip shape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.loginphoto),
                    contentDescription = "Gourmetia Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }


            Text(
                text = "Bienvenue",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE54D70),
                modifier = Modifier.padding(vertical = 32.dp)
            )

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
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

            // Password TextField
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFF597B)
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFFF597B),
                    focusedBorderColor = Color(0xFFFF597B),
                    unfocusedLabelColor = Color(0xFFFF597B),
                    focusedLabelColor = Color(0xFFFF597B)
                )
            )

            TextButton(
                onClick = { navController.navigate(Screen.ForgotPassword.route) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    "Mot de passe oublié ?",
                    color = Color(0xFFFF597B),
                    fontSize = 12.sp
                )
            }

            // Login Button with gradient background
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        authViewModel.login(
                            context = context,
                            email = email,
                            password = password,
                            onSuccess = {
                                try {
                                    val accessToken = authViewModel.getAccessToken(context)
                                    val userId = authViewModel.getUserId(context)
                                    navController.navigate("home") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                } catch (e: Exception) {
                                    Log.e("AuthScreen", "Navigation failed", e)
                                    Toast.makeText(context, "Navigation failed: ${e.message}", Toast.LENGTH_LONG).show()
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
                    .height(70.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(12.dp),
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
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Se connecter",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }

            TextButton(
                onClick = { navController.navigate(Screen.SignUp.route) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Pas encore de compte ? ",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        "S'inscrire",
                        fontSize = 14.sp,
                        color = Color(0xFFFF597B),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    AuthScreen(navController = rememberNavController())
}