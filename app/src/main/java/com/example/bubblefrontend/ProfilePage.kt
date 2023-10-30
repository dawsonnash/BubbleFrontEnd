package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.widget.ConstraintLayout
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
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
    val errorMessage =
        remember { mutableStateOf<String?>(null) } // Added to store the error message

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
    val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    val name = profileSharedPreferences.getString("name", "")
    val username = profileSharedPreferences.getString("username", "")

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
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = username ?: "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    SettingsIcon()
                }

                ProfileImageFollowersFollowing(context, profileSharedPreferences)


                Text(
                    text = "${profile.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                // Bio
                Row(modifier = Modifier.padding(end = 16.dp)) {
                    Text(
                        text = "${profile.bio}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    EditProfileButton(context)
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    BottomDashboard()
                }
            }
        }

    } else if (errorMessage.value != null) {
        // Display the error message
        Text(text = "Error: ${errorMessage.value}")
    } else {
        // Loading state
        Text("Loading...")
    }
}

@Composable
fun ProfileImageFollowersFollowing(context: Context, profileSharedPreferences: SharedPreferences) {

    val followers = profileSharedPreferences.getInt("followers", -1)
    val following = profileSharedPreferences.getInt("following", -1)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        ProfileIcon(context, Modifier.padding(start = 16.dp, top = 16.dp))

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$followers", fontWeight = FontWeight.Bold, fontSize = 20.sp) // Dynamic value for actual count
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Followers", fontSize = 20.sp,)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "$following", fontWeight = FontWeight.Bold, fontSize = 20.sp) // Dynamic value for actual count
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Following", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfileIcon(context: Context, modifier: Modifier = Modifier) {

    // Probably put this functionality in the API handler
    val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    val imageURL = profileSharedPreferences.getString("profile_picture", "")
    val baseURL = "http://54.202.77.126:8080"
    val fullImageURL = baseURL + imageURL

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
            painter = rememberImagePainter(fullImageURL),  // Using Coil to load an image from a URL
            contentDescription = "Profile Picture",
            modifier = Modifier.size(100.dp),
            contentScale = ContentScale.Crop
        )
    }
}
@Composable
fun EditProfileButton(context: Context){
    Button(
        onClick = {
            val intent = Intent(context, EditProfilePage::class.java)
            context.startActivity(intent)

        },
    // Make button transparent
    ){
        Image(
            painter = painterResource(id = R.drawable.baseline_edit_24),
            contentDescription = "Profile Picture",
        )
        Text(
            text = "Edit Profile",
            fontSize = 10.sp
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
            .size(24.dp)
            .clickable {
                val intent = Intent(context, SettingsPage::class.java)
                context.startActivity(intent)
            }
    )
}
