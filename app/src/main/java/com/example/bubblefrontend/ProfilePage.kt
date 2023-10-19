package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.api.ProfileResponse
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class ProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                ProfileScreen()
            }
        }
    }
}

@Composable
fun ProfileScreen() {
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
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start,  // Align to the start (or left)
                verticalArrangement = Arrangement.Top

            ) {
                // Top Bar for Settings Icon, Profile Picture, and Friends Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileIcon(
                        Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                    SettingsIcon()
                }
                Text(
                    text = "${profile.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                // Bio
                Text(
                    text = "${profile.bio}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                // This Spacer will take all available space, pushing the BottomDashboard to the bottom
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Box(modifier = Modifier
                    .padding(16.dp)
                ) {
                    BottomDashboard()
                }
            }
        }

    } else if (errorMessage.value != null) {
        // Error state - display the error message
        Text(text = "Error: ${errorMessage.value}")
    } else {
        // Loading state
        Text("Loading...")
    }
}

@Composable
fun ProfileIcon(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.Gray)
            .padding(10.dp)
            .shadow(4.dp, CircleShape),
        color = Color.White
    ) {
        Image(
            painter = painterResource(id = R.drawable.profile_icon), // Your drawable resource
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
    }
}


@Composable
fun SettingsIcon() {
    val context = LocalContext.current
    Icon(
        imageVector = Icons.Default.Settings,
        contentDescription = "Settings",
        modifier = Modifier
            .padding(16.dp)
            .size(24.dp)
            .clickable {
                val intent = Intent(context, SettingsPage::class.java)
                context.startActivity(intent)
            }
    )
}
