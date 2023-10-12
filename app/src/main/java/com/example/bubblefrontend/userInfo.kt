package com.example.bubblefrontend

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
