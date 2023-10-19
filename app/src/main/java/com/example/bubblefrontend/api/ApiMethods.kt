package com.example.bubblefrontend.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiMethods {
    @POST("login")
    fun authenticateLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("register")
    fun registerUser(@Body registrationRequest: RegistrationRequest): Call<RegistrationResponse>

    @GET("/account/{username}")
    fun getProfile(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String
    ): Call<ProfileResponse>

    @POST("/account/{username}")
    // Maybe look into suspend fun?
    fun editProfile(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Body request: EditProfileRequest
    ): Call<EditProfileResponse>

}

