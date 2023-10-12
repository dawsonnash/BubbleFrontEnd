package com.example.bubblefrontend

import ApiMethods
import android.content.Context
import android.content.Intent
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiHandler {

    fun handleLogin(username: String, password: String, context: Context) {

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
                        // Have token, need to storemaybe? Look into SharedPreferences
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

}
