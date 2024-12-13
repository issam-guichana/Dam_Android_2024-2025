package com.example.gourmetia.ViewModels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import androidx.compose.runtime.mutableStateOf
import com.example.gourmetia.Screens.FavouriteRecipe
import com.example.gourmetia.remote.DeleteUserResponse
import com.example.gourmetia.remote.GenerateEmailRequest
import com.example.gourmetia.remote.GenerateEmailResponse
import com.example.gourmetia.remote.GetUserResponse
import com.example.gourmetia.remote.LoginRequest
import com.example.gourmetia.remote.LoginResponse
import com.example.gourmetia.remote.RetrofitInstance
import com.example.gourmetia.remote.SignUpRequest
import com.example.gourmetia.remote.SignUpResponse
import com.example.gourmetia.remote.UpdateProfileRequest
import com.example.gourmetia.remote.UpdateProfileResponse
import com.example.gourmetia.remote.UserData
import com.example.gourmetia.remote.VerifyEmailRequest
import com.example.gourmetia.remote.VerifyEmailResponse

class AuthViewModel : ViewModel() {
    var loginResponse = mutableStateOf<String?>(null)
    var signupResponse = mutableStateOf<String?>(null)
    var forgotPasswordResponse = mutableStateOf<String?>(null)
    var isLoading = mutableStateOf(false)
    var verifyEmailResponse = mutableStateOf<String?>(null)
    var deleteAccountResponse = mutableStateOf<String?>(null)
    var updateProfileResponse = mutableStateOf<String?>(null)
    var userDataResponse = mutableStateOf<GetUserResponse?>(null)

    companion object {
        private const val PREF_ACCESS_TOKEN = "access_token"
        private const val PREF_REFRESH_TOKEN = "refresh_token"
        private const val PREF_USER_ID = "user_id"
    }

    fun login(
        context: Context,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response: Response<LoginResponse> = RetrofitInstance.api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        // Save the login details to SharedPreferences
                        saveLoginDetails(
                            context,
                            loginResponse.accessToken,
                            loginResponse.refreshToken,
                            loginResponse.userId
                        )
                    }
                    this@AuthViewModel.loginResponse.value = "Success: Login successful"
                    onSuccess()
                } else {
                    val errorMessage = "Login failed: ${response.errorBody()?.string()}"
                    this@AuthViewModel.loginResponse.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "Network error: ${e.message}"
                loginResponse.value = errorMessage
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                loginResponse.value = errorMessage
                onError(errorMessage)
            } finally {
                isLoading.value = false
            }
        }
    }
    private fun saveLoginDetails(
        context: Context,
        accessToken: String,
        refreshToken: String,
        userId: String
    ) {
        val prefs = getSharedPreferences(context)
        prefs.edit().apply {
            putString(PREF_ACCESS_TOKEN, accessToken)
            putString(PREF_REFRESH_TOKEN, refreshToken)
            putString(PREF_USER_ID, userId)
            apply()
        }
    }

    // Helper functions to retrieve stored values
    fun getAccessToken(context: Context): String? {
        return getSharedPreferences(context).getString(PREF_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(context: Context): String? {
        return getSharedPreferences(context).getString(PREF_REFRESH_TOKEN, null)
    }

    fun getUserId(context: Context): String? {
        return getSharedPreferences(context).getString(PREF_USER_ID, null)
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    fun logout(
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Clear auth token and user data from SharedPreferences
                val prefs = getSharedPreferences(context)
                prefs.edit().apply {
                    clear()
                    apply()
                }

                // Clear all responses
                clearResponses()
                onSuccess()
            } finally {
                isLoading.value = false
            }
        }
    }


    fun signup(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response: Response<SignUpResponse> = RetrofitInstance.api.signUp(
                    SignUpRequest(name, email, password,confirmPassword)
                )
                if (response.isSuccessful ) {
                    val successMessage = "Success: ${response.body()?.message ?: "Account created successfully"}"
                    signupResponse.value = successMessage
                    onSuccess()
                } else {
                    val errorMessage = "Signup failed: ${response.body()?.message ?: response.errorBody()?.string()}"
                    signupResponse.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "Network error: ${e.message}"
                signupResponse.value = errorMessage
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                signupResponse.value = errorMessage
                onError(errorMessage)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun generateEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response: Response<GenerateEmailResponse> = RetrofitInstance.api.generateEmail(
                    GenerateEmailRequest(email)
                )
                if (response.isSuccessful ) {
                    val successMessage = "Success: ${response.body()?.message ?: "Email sent successfully"}"
                    forgotPasswordResponse.value = successMessage
                    onSuccess()
                } else {
                    val errorMessage = "Failed: ${response.body()?.message ?: response.errorBody()?.string()}"
                    forgotPasswordResponse.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "Network error: ${e.message}"
                forgotPasswordResponse.value = errorMessage
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                forgotPasswordResponse.value = errorMessage
                onError(errorMessage)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun verifyEmail(
        context: Context,
        otp: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Get the user ID from SharedPreferences
                val userId = getUserId(context)
                if (userId == null) {
                    onError("User ID not found")
                    return@launch
                }

                val response: Response<VerifyEmailResponse> = RetrofitInstance.api.verifyEmail(
                    userId,
                    VerifyEmailRequest(otp)
                )
                if (response.isSuccessful) {
                    val successMessage = "Success: ${response.body()?.message ?: "Email verified successfully"}"
                    verifyEmailResponse.value = successMessage
                    onSuccess()
                } else {
                    val errorMessage = "Verification failed: ${response.body()?.message ?: response.errorBody()?.string()}"
                    verifyEmailResponse.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "Network error: ${e.message}"
                verifyEmailResponse.value = errorMessage
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                verifyEmailResponse.value = errorMessage
                onError(errorMessage)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteAccount(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Get the user ID from SharedPreferences
                val userId = getUserId(context)
                if (userId == null) {
                    onError("User ID not found")
                    return@launch
                }

                val response: Response<DeleteUserResponse> = RetrofitInstance.api.deleteUser(userId)

                if (response.isSuccessful) {
                    // Clear local storage
                    val prefs = getSharedPreferences(context)
                    prefs.edit().clear().apply()

                    // Clear all responses
                    clearResponses()
                    onSuccess()
                } else {
                    val errorMessage = "Failed to delete account: ${response.errorBody()?.string()}"
                    deleteAccountResponse.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "Network error: ${e.message}"
                deleteAccountResponse.value = errorMessage
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                deleteAccountResponse.value = errorMessage
                onError(errorMessage)
            } finally {
                isLoading.value = false
            }
        }
    }


    fun updateProfile(
        context: Context,
        name: String,
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val userId = getUserId(context)
                if (userId == null) {
                    onError("User ID not found")
                    return@launch
                }

                val response: Response<UpdateProfileResponse> = RetrofitInstance.api.updateProfile(
                    userId,
                    UpdateProfileRequest(name, email)
                )

                if (response.isSuccessful && response.body() != null) {
                    val successMessage = "Success: ${response.body()?.message ?: "Profile updated successfully"}"
                    updateProfileResponse.value = successMessage

                    // Update local storage if needed
                    response.body()?.user?.let { userData ->
                        // You might want to update some local storage with the new user data
                        saveUserData(context, userData)
                    }

                    onSuccess()
                } else {
                    val errorMessage = "Update failed: ${response.errorBody()?.string()}"
                    updateProfileResponse.value = errorMessage
                    onError(errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "Network error: ${e.message}"
                updateProfileResponse.value = errorMessage
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                updateProfileResponse.value = errorMessage
                onError(errorMessage)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun getUserById(
        context: Context,
        onSuccess: (GetUserResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val userId = getUserId(context)
                if (userId == null) {
                    onError("User ID not found")
                    return@launch
                }

                val response = RetrofitInstance.api.getUserById(userId)
                if (response.isSuccessful) {
                    response.body()?.let { userData ->
                        userDataResponse.value = userData
                        // Save user data to SharedPreferences
                        saveUserData(context, userData.user)
                        onSuccess(userData)
                    } ?: onError("No user data found")
                } else {
                    val errorMessage = "Failed to fetch user data: ${response.errorBody()?.string()}"
                    onError(errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "Network error: ${e.message}"
                onError(errorMessage)
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                onError(errorMessage)
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun saveUserData(context: Context, userData: UserData) {
        val prefs = getSharedPreferences(context)
        prefs.edit().apply {
            putString("user_id", userData._id)
            putString("user_name", userData.name)
            putString("user_email", userData.email)
            apply()
        }
    }

    fun toggleBookmark(
        context: Context,
        recipeId: String,
        recipe: FavouriteRecipe,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading.value = true
            Log.d("toggleBookmark", "Function started")
            try {
                val userId = getUserId(context)
                Log.d("toggleBookmark", "UserId retrieved: $userId")

                if (userId == null) {
                    Log.e("toggleBookmark", "User not logged in")
                    onError("User not logged in")
                    return@launch
                }

                Log.d("toggleBookmark", "Calling API with recipeId: $recipeId and recipe: $recipe")
                val response = RetrofitInstance.api.toggleBookmark(
                    userId = userId,
                    recipeId = recipeId,
                    recipe = recipe
                )

                if (response.isSuccessful) {
                    Log.d("toggleBookmark", "API call successful")
                    onSuccess()
                } else {
                    val errorMessage = "Failed to bookmark recipe: ${response.errorBody()?.string()}"
                    Log.e("toggleBookmark", errorMessage)
                    onError(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "Error: ${e.message}"
                Log.e("toggleBookmark", errorMessage, e)
                onError(errorMessage)
            } finally {
                isLoading.value = false
                Log.d("toggleBookmark", "Loading state set to false")
            }
        }
    }


    // Update clearResponses to include forgotPasswordResponse
    fun clearResponses() {
        loginResponse.value = null
        signupResponse.value = null
        forgotPasswordResponse.value = null
        verifyEmailResponse.value = null
        deleteAccountResponse.value = null
        updateProfileResponse.value = null
    }
}