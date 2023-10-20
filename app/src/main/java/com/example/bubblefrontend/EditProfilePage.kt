package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class EditProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                EditProfileScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    var newBio by remember { mutableStateOf("") }

    // Read account data from accountSharedPreferences
    val accountSharedPreferences = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
    val token = accountSharedPreferences.getString("token", "")
    val username = accountSharedPreferences.getString("username", "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable {
                    // Navigating back to the ProfilePage
                    val intent = Intent(context, ProfilePage::class.java)
                    context.startActivity(intent)
                }
            )
            Text(text = "Edit Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        OutlinedTextField(
            value = newBio,
            onValueChange = { newBio = it },
            label = { Text("Edit Bio") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Dismiss the keyboard maybe?

                    // Send the bio update request
                    val apiHandler = ApiHandler()
                    if (token != null && username != null) {
                            apiHandler.handleEditProfile(token, newBio, username, context)
                    }
                    else(
                            Toast.makeText(context, "Something went WRONG dawg", Toast.LENGTH_LONG)
                                .show()

                    )
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))


        // Adding the BottomDashboard at the bottom
        BottomDashboard()
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}
