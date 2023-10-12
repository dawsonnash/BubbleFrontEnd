package com.example.bubblefrontend.api

data class LoginRequest(
    val username: String,
    val password: String)
data class LoginResponse(val token: String)
data class RegistrationRequest(
    val username: String,
    val password: String,
    val email: String,
    val name: String
)
data class RegistrationResponse(val message: String)
