package com.example.bubblefrontend.api

data class LoginRequest(
    val username: String,
    val password: String)
data class LoginResponse(val token: String)
data class RegistrationRequest(
    val email: String,
    val name: String,
    val username: String,
    val password: String
)
data class RegistrationResponse(val message: String)

data class ProfileResponse(
    val name: String,
    val username: String,
    val profilePicture: String,
    val bio: String,
    val accountCreated: String,
    val editable: Boolean
)

