package com.example.gourmetia.Screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import coil.compose.rememberAsyncImagePainter
import com.example.gourmetia.Navigation.Screen
import com.example.gourmetia.R
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import android.os.Parcelable
import android.util.Log
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Updated Ingredient class to focus on name and quantity
@Serializable
@Parcelize
data class Ingredient(
    val name: String,
    val quantity: String = "Not specified"
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientIdentificationScreen(navController: NavController) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var ingredientText by remember { mutableStateOf("") }
    var analysisResult by remember { mutableStateOf<List<Ingredient>>(emptyList()) }
    var manualIngredientText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Updated parsing function to extract name and quantity
    fun parseIngredientsResponse(response: String): List<Ingredient> {
        return response.split("\n")
            .filter { it.contains(":") }
            .mapNotNull { line ->
                val parts = line.split(":")
                if (parts.size >= 2) {
                    Ingredient(
                        name = parts[0].trim(),
                        quantity = parts.getOrNull(1)?.trim() ?: "Not specified"
                    )
                } else null
            }
    }

    suspend fun analyzeIngredients(
        imageUri: Uri? = null,
        textInput: String = ""
    ): List<Ingredient> {
        return withContext(Dispatchers.IO) {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = "AIzaSyB-lwjXMpc6O-pb7ZYSkXpNynowfQLwKKU"
                )

                val prompt = when {
                    imageUri != null -> {
                        val bitmap = android.graphics.BitmapFactory.decodeStream(
                            context.contentResolver.openInputStream(imageUri)
                        )
                        val response = generativeModel.generateContent(
                            content {
                                image(bitmap)
                                text(
                                    """
                                Carefully analyze this image of ingredients. 
                                For each ingredient, provide:
                                1. Precise name
                                2. Estimated quantity
                                
                                Respond in a format like:
                                Ingredient Name: Quantity
                                """.trimIndent()
                                )
                            }
                        )
                        response.text ?: "No ingredients identified."
                    }

                    textInput.isNotBlank() -> {
                        val response = generativeModel.generateContent(
                            """
                            Analyze the following ingredient list: $textInput
                            
                            For each ingredient, provide:
                            1. Precise name
                            2. Estimated quantity
                            
                            Respond in a format like:
                            Ingredient Name: Quantity
                            """.trimIndent()
                        )
                        response.text ?: "Unable to analyze ingredients."
                    }

                    else -> "Please provide an image or ingredient list."
                }

                // Parse the response into Ingredient objects
                parseIngredientsResponse(prompt)
            } catch (e: Exception) {
                listOf(
                    Ingredient(
                        name = "Error",
                        quantity = "Unable to analyze: ${e.localizedMessage}"
                    )
                )
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
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Ingredient Identifier", color = Color.White) },
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
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo and Title in a white rounded card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gourmetia_logo),
                            contentDescription = "Gourmet AI Logo",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Ingredient Identifier",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = Color(0xFFFF597B)
                            )
                        )
                    }
                }

                // Image Upload Button
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Upload Image")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Upload Ingredient Image")
                        }
                    }
                }

                // Selected Image Preview
                selectedImageUri?.let { uri ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(model = uri),
                            contentDescription = "Selected Ingredients",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Manual Input TextField
                OutlinedTextField(
                    value = ingredientText,
                    onValueChange = { ingredientText = it },
                    label = { Text("Enter ingredients manually") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFFF597B),
                        focusedBorderColor = Color(0xFFFF597B),
                        unfocusedLabelColor = Color(0xFFFF597B),
                        focusedLabelColor = Color(0xFFFF597B)
                    )
                )

                // Analyze Button
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            isLoading = true
                            analysisResult = analyzeIngredients(
                                imageUri = selectedImageUri,
                                textInput = ingredientText
                            )
                            isLoading = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Analyze")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Analyze Ingredients")
                            }
                        }
                    }
                }

                // Results Card
                if (analysisResult.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .height(200.dp)
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = "Ingredient Analysis",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF597B)
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            analysisResult.forEach { ingredient ->
                                Text(
                                    text = "• ${ingredient.name}: ${ingredient.quantity}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                    }

                    // Manual Ingredient Addition
                    OutlinedTextField(
                        value = manualIngredientText,
                        onValueChange = { manualIngredientText = it },
                        label = { Text("Add more ingredients manually") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFFF597B),
                            focusedBorderColor = Color(0xFFFF597B),
                            unfocusedLabelColor = Color(0xFFFF597B),
                            focusedLabelColor = Color(0xFFFF597B)
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (manualIngredientText.isNotBlank()) {
                                        val newIngredient = Ingredient(
                                            name = manualIngredientText.trim(),
                                            quantity = "Manually added"
                                        )
                                        analysisResult = analysisResult + newIngredient
                                        manualIngredientText = ""
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Add Ingredient",
                                    tint = Color(0xFFFF597B)
                                )
                            }
                        }
                    )

                    // Generate Recipe Button
                    Button(
                        onClick = {
                            val allIngredients = analysisResult.toMutableList()
                            if (manualIngredientText.isNotBlank()) {
                                allIngredients.add(
                                    Ingredient(
                                        name = manualIngredientText.trim(),
                                        quantity = "Manually added"
                                    )
                                )
                            }
                            val cleanedIngredients = allIngredients.filter {
                                it.name.isNotBlank() &&
                                        it.name != "Here's a breakdown of the ingredients in the image, with name and estimated quantity" &&
                                        it.name != "**Ingredient Name"
                            }
                            val ingredientsJson = Json.encodeToString(cleanedIngredients)
                            val encodedIngredientsJson = java.net.URLEncoder.encode(ingredientsJson, "UTF-8")
                            navController.navigate("${Screen.RecipeGeneration.route.replace("{ingredientsList}", encodedIngredientsJson)}")
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(vertical = 8.dp)
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Restaurant, contentDescription = "Generate Recipe")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Recipe")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GeminiChatScreenPreview() {
    IngredientIdentificationScreen(navController = rememberNavController())
}

