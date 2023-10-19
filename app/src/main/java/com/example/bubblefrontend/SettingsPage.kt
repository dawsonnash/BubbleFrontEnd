package com.example.bubblefrontend

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.api.ProfileResponse
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class SettingsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
               //SettingsScreen()
                UserProfile()
            }
        }
    }
}

@Composable
fun SettingsScreen() {

    val context = LocalContext.current              // For transitioning to other activities

    var storedToken by remember { mutableStateOf("") }
    var storedUsername by remember { mutableStateOf("") }

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
    storedToken = sharedPreferences.getString("token", "No token found") ?: "No token found"
    storedUsername = sharedPreferences.getString("username", "No username found") ?: "No username found"


    Column() {
        Text("Stored Token: $storedToken")
        Text(text = "Username: $storedUsername")
    }



}

@Composable
fun UserProfile() {
    val context = LocalContext.current
    val profileData = remember { mutableStateOf<ProfileResponse?>(null) }
    val errorMessage = remember { mutableStateOf<String?>(null) } // Added to store the error message

    LaunchedEffect(Unit) {
        val apiHandler = ApiHandler()
        apiHandler.handleProfile(context,
            onSuccess = { profile ->
                profileData.value = profile
            },
            onError = { error ->
                errorMessage.value = error // Store the error message
            }
        )
    }

    // Read profile data from SharedPreferences
    val sharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    val name = sharedPreferences.getString("name", "")
    val username = sharedPreferences.getString("username", "")

    if (profileData.value != null) {
        // UI to display profile
        val profile = profileData.value!!

        Text(text = "Name: ${profile.name}")
        Text(text = "Username: ${profile.username}")
        // Text(text = "Bio: ${profile.bio}")
        // And so on
    } else if (errorMessage.value != null) {
        // Error state - display the error message
        Text(text = "Error: ${errorMessage.value}")
    } else {
        // Loading state
        Text("Loading...")
    }
}
