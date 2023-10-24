package com.example.bubblefrontend.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiMethods {
    @POST("api/login")
    fun authenticateLogin(
        @Body loginRequest: LoginRequest
    ): Call<LoginResponse>

    @POST("api/register")
    fun registerUser(
        @Body registrationRequest: RegistrationRequest
    ): Call<RegistrationResponse>

    @GET("api/account/{username}")
    fun getProfile(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String
    ): Call<ProfileResponse>


    // Server side is .put
    @PUT("api/account/{username}")
// Maybe look into suspend fun?
    fun editProfile(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String,
        @Body editProfileRequest: EditProfileRequest
    ): Call<EditProfileResponse>
}



