package com.example.bubblefrontend.api

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.bubblefrontend.GlobalPage
import com.example.bubblefrontend.LoginPage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

                        // Putting the entered username into SharedPreferences, NOT a response string from the server
                        editor.putString("username", username)

                        // Storing isLoggedIn as true.
                        editor.putBoolean("isLoggedIn", true)

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
                Log.d("Debug", "Network error details: ${t.localizedMessage}")
                Toast.makeText(context, "Network error", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun handleRegistration(email: String, firstName: String, username: String, password: String, context: Context){

        val retrofit = Retrofit.Builder()
            .baseUrl("http://54.202.77.126:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiMethods::class.java)

        val registrationRequest = RegistrationRequest(email, firstName, username, password)  // Need to add last name
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

            // Standard Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl("http://54.202.77.126:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Using API - always have to make an instance
            val apiService = retrofit.create(ApiMethods::class.java)

            // Initialize SharedPreferences for profile data
            val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
            val profileEditor = profileSharedPreferences.edit()

            // Access stored token and username from existing "Account Details" in SharedPreferences for server call
            val accountSharedPreferences = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
            val token = accountSharedPreferences.getString("token", "") ?: ""
            val storedUsername = accountSharedPreferences.getString("username", "")

            // For errors - "Account doesn't exist" so tryna log it out
            Log.d("Debug", "Stored Username: $storedUsername, Token: $token")

            // Begin server call
            val call = storedUsername?.let { apiService.getProfile("Bearer $token", it) }

            // Response from server. Success or failure logic
            call?.enqueue(object : Callback<ProfileResponse> {
                override fun onResponse(call: Call<ProfileResponse>, response: Response<ProfileResponse>) {

                    if (response.isSuccessful) {

                        // Stores the JSON response from the server into the ProfileResponse data class
                        val profileResponse = response.body()


                        // Check if profileResponse is not null. I.e, does the account exist & does it have data
                        if (profileResponse != null) {

                            // Store the profile data in the "ProfileData" SharedPreferences file
                            profileEditor.putString("username", profileResponse.username)
                            profileEditor.putInt("uid", profileResponse.uid)
                            profileEditor.putString("name", profileResponse.name)
                            profileEditor.putString("profile_picture", profileResponse.profile_picture)
                            profileEditor.putString("url", profileResponse.url)
                            profileEditor.putString("html_url", profileResponse.html_url)
                            profileEditor.putString("followers_url", profileResponse.followers_url)
                            profileEditor.putString("following_url", profileResponse.following_url)
                            profileEditor.putBoolean("bubble_admin", profileResponse.bubble_admin)
                            profileEditor.putString("email", profileResponse.email)
                            profileEditor.putString("bio", profileResponse.bio)
                            profileEditor.putInt("followers", profileResponse.followers)
                            profileEditor.putInt("following", profileResponse.following)
                            profileEditor.putString("created_at", profileResponse.created_at)
                            profileEditor.putString("last_accessed_at", profileResponse.last_accessed_at)
                            profileEditor.putBoolean("editable", profileResponse.editable)
                            profileEditor.apply()

                            // Log the profile data for debugging
                            Log.d("Debug", "Username: ${profileResponse.username}")
                            Log.d("Debug", "UID: ${profileResponse.uid}")
                            Log.d("Debug", "Name: ${profileResponse.name}")
                            Log.d("Debug", "Profile Picture: ${profileResponse.profile_picture}")
                            Log.d("Debug", "URL: ${profileResponse.url}")
                            Log.d("Debug", "HTML URL: ${profileResponse.html_url}")
                            Log.d("Debug", "Followers URL: ${profileResponse.followers_url}")
                            Log.d("Debug", "Following URL: ${profileResponse.following_url}")
                            Log.d("Debug", "Bubble Admin: ${profileResponse.bubble_admin}")
                            Log.d("Debug", "Email: ${profileResponse.email}")
                            Log.d("Debug", "Bio: ${profileResponse.bio}")
                            Log.d("Debug", "Followers: ${profileResponse.followers}")
                            Log.d("Debug", "Following: ${profileResponse.following}")
                            Log.d("Debug", "Created At: ${profileResponse.created_at}")
                            Log.d("Debug", "Last Accessed At: ${profileResponse.last_accessed_at}")
                            Log.d("Debug", "Editable: ${profileResponse.editable}")

                            // This tells the profile page that it was a success, in the LaunchedEffect coroutine
                            onSuccess(profileResponse)
                        } else {
                            onError("Failed to retrieve profile data")
                        }
                    } else {
                        // Handle API doc errors
                        val errorBody = response.errorBody()?.string()
                        Log.d("Debug", "Error Body: $errorBody")

                        val errorMessage = when (response.code()) {
                            404 -> "Account does not exist"
                            500 -> "Internal Server Error, bruh"
                            else -> "Unknown error occurred"
                        }
                        onError(errorMessage)
                    }
                }

                override fun onFailure(call: Call<ProfileResponse>, t: Throwable) {
                    onError("Network error, bro!")
                }
            })
        }

        fun handleEditProfile(newBio: String, newName: String, imageUri: Uri?, context: Context) {

            val retrofit = Retrofit.Builder()
                .baseUrl("http://54.202.77.126:8080")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiMethods::class.java)

            val accountSharedPreferences = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
            val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

            val token = accountSharedPreferences.getString("token", "") ?: ""
            val storedUsername = accountSharedPreferences.getString("username", "") ?: ""
            val oldName = profileSharedPreferences.getString("name", "") ?: ""
            val oldBio = profileSharedPreferences.getString("bio", "") ?: ""


            // Was error - "Account doesn't exist" so tryna log it out
            Log.d("Debug", "Stored Username: $storedUsername, Token: $token, Name: $newName, Bio: $newBio")

            // Check to see if user did not enter name in field
            val newBioPart = newBio.ifBlank { oldBio }.toRequestBody(MultipartBody.FORM)
            val newNamePart = newName.ifBlank { oldName }.toRequestBody(MultipartBody.FORM)

            // All for uploading the picture. It takes the URI and converts it to a bytearray to be sent
            val imagePart: MultipartBody.Part? = imageUri?.let { uri ->
                val byteArray = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                }

                byteArray?.let {
                    val mediaType = "image/jpeg".toMediaTypeOrNull()
                    val requestFile = it.toRequestBody(mediaType)
                    MultipartBody.Part.createFormData("image", "user_image.jpg", requestFile)
                }
            }


            // This is exactly what is being seent
            Log.d("Debug", "Sending request with: Token: $token, Username: $storedUsername, New Bio: $newBio, New Name: $newName")

            storedUsername.let {
                apiService.editProfile(
                    "Bearer $token",
                    it,
                    newBioPart,
                    newNamePart,
                    imagePart
                )
            }.also {
                it.enqueue(object : Callback<EditProfileResponse> {
                    override fun onResponse(
                        call: Call<EditProfileResponse>,
                        response: Response<EditProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            val editProfileResponse = response.body()
                            val message = editProfileResponse?.message

                            if (!message.isNullOrEmpty()) {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.d("Debug", "Error Body: $errorBody")

                            when (response.code()) {
                                400 -> Toast.makeText(context, "Unable to update bio", Toast.LENGTH_LONG).show()
                                404 -> Toast.makeText(context, "Account does not exist", Toast.LENGTH_LONG).show()
                                500 -> Toast.makeText(context, "Internal server error", Toast.LENGTH_LONG).show()
                                else -> Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
                            }

                            Log.d("Debug", "HTTP Status Code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<EditProfileResponse>, t: Throwable) {
                        Log.d("Debug", "Network error details: ${t.localizedMessage}")
                        Toast.makeText(context, "Network error, bruh", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

}
