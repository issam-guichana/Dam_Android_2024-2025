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



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ingredient Identifier") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF0F4F8),
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF0F4F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Add vertical scrolling here
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo and Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
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
                        fontSize = 24.sp
                    )
                )
            }

            // Image Upload Section
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5555)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Upload Image")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Upload Ingredient Image")
            }

            // Selected Image Preview
            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Selected Ingredients",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(vertical = 16.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Manual Input Section
            TextField(
                value = ingredientText,
                onValueChange = { ingredientText = it },
                label = { Text("Enter ingredients manually (comma-separated)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = Color(0xFF2ECC71).copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    cursorColor = Color(0xFF2ECC71),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
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
                    containerColor = Color(0xFF2ECC71)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.Search, contentDescription = "Analyze")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Ingredients")
                }
            }


            if (analysisResult.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(top = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ingredient Analysis",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
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

                // New TextField for manually adding ingredients
                TextField(
                    value = manualIngredientText,
                    onValueChange = { manualIngredientText = it },
                    label = { Text("Add more ingredients manually") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFFFF5555).copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        cursorColor = Color(0xFFFF5555),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (manualIngredientText.isNotBlank()) {
                                    val newIngredient = Ingredient(
                                        name = manualIngredientText.trim(),
                                        quantity = "Manually added"
                                    )
                                    analysisResult = analysisResult + newIngredient
                                    manualIngredientText = "" // Clear the text field
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.AddPhotoAlternate,
                                contentDescription = "Add Ingredient",
                                tint = Color(0xFFFF5555)
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(100.dp))
                Button(
                    onClick = {
                        // Combine analysisResult and manually added ingredient if exists
                        val allIngredients = analysisResult.toMutableList()
                        if (manualIngredientText.isNotBlank()) {
                            allIngredients.add(
                                Ingredient(
                                    name = manualIngredientText.trim(),
                                    quantity = "Manually added"
                                )
                            )
                        }

                        // Remove problematic entries
                        val cleanedIngredients = allIngredients.filter {
                            it.name.isNotBlank() &&
                                    it.name != "Here's a breakdown of the ingredients in the image, with name and estimated quantity" &&
                                    it.name != "**Ingredient Name"
                        }

                        // Log cleaned ingredients
                        Log.d("IngredientScreen", "Cleaned ingredients: ${cleanedIngredients.size}")
                        cleanedIngredients.forEach {
                            Log.d("IngredientScreen", "Ingredient: ${it.name}, Quantity: ${it.quantity}")
                        }
                        // Convert to JSON
                        val ingredientsJson = Json.encodeToString(cleanedIngredients)

                        // URL encode the JSON to prevent navigation issues
                        val encodedIngredientsJson = java.net.URLEncoder.encode(ingredientsJson, "UTF-8")

                        // Navigate with encoded ingredients JSON
                        navController.navigate("${Screen.RecipeGeneration.route.replace("{ingredientsList}", encodedIngredientsJson)}")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2ECC71)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = "Generate Recipe")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Recipe with These Ingredients")
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

