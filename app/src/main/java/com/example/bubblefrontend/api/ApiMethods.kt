package com.example.bubblefrontend.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Query

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
    @Headers("Accept: application/json")
    fun getProfile(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String
    ): Call<ProfileResponse>

    // Maybe look into suspedn functions
    @Multipart
    @PUT("api/account/{username}")
    fun editProfile(
        @Header("Authorization") authHeader: String,
        @Path("username") username: String,
        @Part("newBio") newBio: RequestBody,
        @Part("newName") newName: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<EditProfileResponse>

    @GET("/api/search")
    @Headers("Accept: application/json")
    fun getAllUsers(
        // Next line is for if we want to implement searching a specific searchTerm
       // @Query("searchTerm") searchTerm: String
    ): Call<List<NonUser>>


}



