// Notes
// -- Registration still allows for null inputs, i.e., no password or username

package com.example.bubblefrontend

import MyApi
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Registration()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Registration() {
    val context = LocalContext.current              // For transitioning to other activities
    val retrofit = Retrofit.Builder()
        .baseUrl("http://54.202.77.126:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(MyApi::class.java)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var firstName by remember { mutableStateOf("") }
        var lastName by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var phoneNumber by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        OutlinedTextField(
            value = firstName,
            onValueChange = { if (it.length <= 32) firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),


        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = { if (it.length <= 32) lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),

        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { if (it.length <= 32) username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { if (it.length <= 10) phoneNumber = it },
            label = { Text("Phone Number") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { if (it.length <= 32) email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { if (it.length <= 32) password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { if (it.length <= 32) confirmPassword = it },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Check if password matches
                // If so, check if username/email is being used
                // If not, add account
                if (password == confirmPassword) {

                    val registrationRequest = RegistrationRequest(email,firstName, username, password)  // Need to add last name
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
                else{
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
                }

            }
        ) {
            Text("Register")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    BubbleFrontEndTheme {
        Registration()
    }
}


