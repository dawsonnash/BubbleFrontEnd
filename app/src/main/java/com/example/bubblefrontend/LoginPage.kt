package com.example.bubblefrontend

import android.os.Bundle
import android.content.Context
import android.content.SharedPreferences
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
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme


class LoginPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen() {
    val context = LocalContext.current              // For transitioning to other activities
    // For storing user info on registration
    // Could delete handleLogin parameters and store SharedPreferences directly in the function
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){

        RisingBubbleAnimation()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username or Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
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
                val apiHandler = ApiHandler()
                apiHandler.handleLogin(username, password, context, editor)
            }
        ) {
            Text("Login")
        }
    }
    }
}


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    BubbleFrontEndTheme {
        LoginScreen()
    }
}


