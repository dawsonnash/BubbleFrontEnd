package com.example.bubblefrontend.api

import android.content.Context
import android.content.SharedPreferences
import android.content.Intent
import android.widget.Toast
import com.example.bubblefrontend.GlobalPage
import com.example.bubblefrontend.LoginPage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiHandler {

    fun handleLogin(username: String, password: String, context: Context, editor: SharedPreferences.Editor) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.202.77.126:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiMethods::class.java)

        val loginRequest = LoginRequest(username, password)

        val call = apiService.authenticateLogin(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val token = loginResponse?.token

                    if (!token.isNullOrEmpty()) {
                        // Successfully authenticated
                        // Storing token
                        editor.putString("token", token)
                        editor.putString("username", username)

                        editor.apply()
                        // Navigate to Global
                        val intent = Intent(context, GlobalPage::class.java)
                        context.startActivity(intent)


                    } else {
                        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show()
                    }
                } else {
                    when (response.code()) {
                        // Error 401
                        401 -> {
                            Toast.makeText(context, "Invalid credentials", Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            // Unexpected errors
                            Toast.makeText(context, "Error: ${response.code()}", Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // for network failures
                Toast.makeText(context, "Network error", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun handleRegistration(username: String, password: String, firstName: String, email: String, context: Context){

        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.202.77.126:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiMethods::class.java)

        val registrationRequest = RegistrationRequest(username, password, firstName, email)  // Need to add last name
        val call = apiService.registerUser(registrationRequest)

        call.enqueue(object : Callback<RegistrationResponse> {
            override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                if (response.isSuccessful) {
                    val registrationResponse = response.body()
                    val message = registrationResponse?.message

                    if (!message.isNullOrEmpty()) {
                        // Registration successful
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        // Navigate to login page
                        val intent = Intent(context, LoginPage::class.java)
                        context.startActivity(intent)
                    }
                } else {
                    // Handle error codes based on API doc
                    when (response.code()) {
                        400 -> {
                            Toast.makeText(context, "Username/email already exists", Toast.LENGTH_LONG).show()
                        }
                        500 -> {
                            Toast.makeText(context, "Registration failed", Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            // Unknown errors
                            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                // network failure
                Toast.makeText(context, "Network error", Toast.LENGTH_LONG).show()
            }
        })

    }

    fun handleProfile(context: Context, onSuccess: (ProfileResponse) -> Unit, onError: (String) -> Unit) {
        // Initialize SharedPreferences for profile data
        val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
        val profileEditor = profileSharedPreferences.edit()

        // Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.202.77.126:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Using API - have to make an instance
        val apiService = retrofit.create(ApiMethods::class.java)

        // Access stored token and username from SharedPreferences
        val sharedPreferences = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "") ?: ""
        val storedUsername = sharedPreferences.getString("username", "")

        // Begin call
        val call = storedUsername?.let { apiService.getProfile("Bearer $token", it) }

        call?.enqueue(object : Callback<ProfileResponse> {
            override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {
                if (response.isSuccessful) {
                    val profileData = response.body()

                    // Check if profileData is not null
                    if (profileData != null) {
                        // Store the profile data in the "ProfileData" SharedPreferences file
                        profileEditor.putString("name", profileData.name)
                        profileEditor.putString("username", profileData.username)
                        profileEditor.putString("profilePicture", profileData.profilePicture)
                        profileEditor.putString("bio", profileData.bio)
                        profileEditor.putString("accountCreated", profileData.accountCreated)
                        profileEditor.putBoolean("editable", profileData.editable)
                        profileEditor.apply()

                        onSuccess(profileData)
                    } else {
                        onError("Failed to retrieve profile data")
                    }
                } else {
                    // Handle API doc errors
                    val errorMessage = when (response.code()) {
                        404 -> "Account does not exist"
                        500 -> "Internal Server Error"
                        else -> "Unknown error occurred"
                    }
                    onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                onError("Network error")
            }
        })
    }


}
