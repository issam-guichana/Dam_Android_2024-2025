package com.example.gourmetia.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface UserAPI {
    @POST("auth/signin")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<SignUpResponse>

    @POST("auth/forgot-password")
    suspend fun generateEmail(@Body request: GenerateEmailRequest): Response<GenerateEmailResponse>

    @POST("auth/verify-otp/{userId}")
    suspend fun verifyEmail(
        @Path("userId") userId: String,
        @Body request: VerifyEmailRequest
    ): Response<VerifyEmailResponse>

    @DELETE("user/{userId}")
    suspend fun deleteUser(@Path("userId") userId: String): Response<DeleteUserResponse>

    @PATCH("user/{userId}")
    suspend fun updateProfile(@Path("userId") userId: String, @Body request: UpdateProfileRequest): Response<UpdateProfileResponse>

    @GET("user/{userId}")
    suspend fun getUserById(@Path("userId") userId: String): Response<GetUserResponse>

    @POST("community/create")
    suspend fun createPost(@Body request: CreatePostRequest): Response<CreatePostResponse>

    @GET("community")
    suspend fun getCommunityPosts(): Response<List<CommunityPostResponse>>
}

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val userId: String
)

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)
data class SignUpResponse(
    val success: Boolean,
    val message: String
)

data class GenerateEmailRequest(
    val email: String
)
data class GenerateEmailResponse(
    val success: Boolean,
    val message: String
)

data class VerifyEmailResponse(
    val success: Boolean,
    val message: String
)

data class DeleteUserResponse(
    val success: Boolean,
    val message: String
)

data class UpdateProfileRequest(
    val name: String,
    val email: String
)
data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UserData
)
data class VerifyEmailRequest(
    val otp: String
)
data class GetUserResponse(
    val success: Boolean,
    val user: UserData
)

data class UserData(
    val _id: String,
    val name: String,
    val email: String,
)

data class CreatePostRequest(
    val content: String,
    val authorId: String
)

data class CreatePostResponse(
    val success: Boolean,
    val message: String
)

data class CommunityPostResponse(
    val _id: String,
    val content: String,
    val author: String,
    val image: String? = null,
    val likes: List<String> = emptyList(), // Changed from Int to List<String>
    val dislikes: List<String> = emptyList(), // Changed from Int to List<String>
    val createdAt: String
)
