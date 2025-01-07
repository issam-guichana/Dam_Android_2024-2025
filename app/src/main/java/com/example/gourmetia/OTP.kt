package com.example.gourmetia

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.gourmetia.Navigation.Screen
import com.example.gourmetia.ViewModels.AuthViewModel

@Composable
fun OTPVerificationScreen(
    navController: NavController,
    email: String,
    authViewModel: AuthViewModel
) {
    var otpCode by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lock Icon
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock Icon",
                tint = Color(0xFFFF597B),
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp)
            )

            // Title
            Text(
                text = "Vérification OTP",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE54D70),
                textAlign = TextAlign.Center
            )

            // Description
            Text(
                text = "Veuillez entrer le code de vérification envoyé à votre adresse e-mail",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // OTP Input Field
            OutlinedTextField(
                value = otpCode,
                onValueChange = { otpCode = it },
                label = { Text("Code OTP") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFF597B)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFFF597B),
                    focusedBorderColor = Color(0xFFFF597B),
                    unfocusedLabelColor = Color(0xFFFF597B),
                    focusedLabelColor = Color(0xFFFF597B)
                )
            )

            // Verify Button
            Button(
                onClick = {
                    if (otpCode.isNotBlank()) {
                        authViewModel.verifyEmail(
                            context = context,
                            otp = otpCode,
                            onSuccess = {
                                navController.navigate(Screen.ForgotPassword.route) {
                                    popUpTo(Screen.OTPVerf.route) { inclusive = true }
                                }
                            },
                            onError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        Toast.makeText(context, "Veuillez entrer le code OTP", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(12.dp)
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
                            shape = MaterialTheme.shapes.small
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Vérifier",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Vous n'avez pas reçu le code ? ",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Renvoyer",
                    fontSize = 14.sp,
                    color = Color(0xFFFF597B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        authViewModel.generateEmail(
                            email = email,
                            onSuccess = {
                                Toast.makeText(context, "Code OTP renvoyé avec succès", Toast.LENGTH_SHORT).show()
                            },
                            onError = { errorMessage ->
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OTPVerificationScreenPreview() {
    OTPVerificationScreen(
        navController = rememberNavController(),
        email = "example@email.com",
        authViewModel = AuthViewModel()
    )
}