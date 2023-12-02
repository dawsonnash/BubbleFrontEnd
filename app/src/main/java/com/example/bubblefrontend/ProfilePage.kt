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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.api.ProfileResponse
import com.example.bubblefrontend.api.RefreshToken
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class ProfilePage : ComponentActivity() {

    // Testing refreshToken
    private lateinit var refreshToken: RefreshToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                ProfileScreen()
            }
            refreshToken = RefreshToken(this)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        refreshToken.unregisterPreferenceChangeListener()
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
    val profilePicture = profileSharedPreferences.getString("profile_picture", "")


    if (profileData.value != null) {
        // UI to display profile
        val profile = profileData.value!!

        // The following sets the background to a specific image

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center

        ) {}
        /*
        {
            // Set the image as the background
            Image(
                painter = painterResource(id = R.drawable.bubblebackground01),
                contentDescription = "Background",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }
         */

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
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
//                           .background(
//                               color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
//                               shape = RoundedCornerShape(4.dp) // Rounded corners
//                           )

                    )
                    Spacer(modifier = Modifier.weight(1f))
                    SettingsIcon()
                }

                if (profilePicture != null) {
                    ProfileImageFollowersFollowing(context, profilePicture, profileSharedPreferences)
                }


                Text(
                    text = "${profile.name}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp)
//                        .background(
//                            color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
//                            shape = RoundedCornerShape(4.dp) // Rounded corners
//
//                        )
                )

                // Bio
                Text(
                    text = "${profile.bio}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp)
//                            .background(
//                                color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
//                                shape = RoundedCornerShape(4.dp) // Rounded corners
//                            )
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Box(
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
fun ProfileImageFollowersFollowing(context: Context, profilePicture: String, profileSharedPreferences: SharedPreferences) {

    val followers = profileSharedPreferences.getInt("followers", -1)
    val following = profileSharedPreferences.getInt("following", -1)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        ProfileIcon(context, profilePicture, Modifier
            .padding(start = 16.dp, top = 16.dp)
            )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
//                .background(
//                    color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
//                     shape = RoundedCornerShape(4.dp))
        ) {
            Text(
                text = "$followers",  // Dynamic value for actual count
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Followers",
                style = TextStyle(
                fontSize = 20.sp,
                color = Color.White)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier
//                .background(
//                    color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
//                    shape = RoundedCornerShape(4.dp) // Rounded corners
        ) {
            Text(text = "$following", // Dynamic value for actual count
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Following",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = Color.White
                ),
                )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ProfileIcon(context: Context, profilePicture: String, modifier: Modifier = Modifier) {

    val baseURL = "http://54.202.77.126:8080"
    val fullImageURL = baseURL + profilePicture

    Surface(
        modifier = modifier
            .size(130.dp)
            .clip(CircleShape)
        // The following applies a background to the profile image
            /*
            .background(
                color = Color.White.copy(alpha = 0.6f), // Semi-transparent white

            )
            .padding(10.dp)
            .shadow(4.dp, CircleShape),

        color = Color.White
        */

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
            contentDescription = "Edit Profile",
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
            .size(30.dp)
//            .background(
//                color = Color.White.copy(alpha = 0.9f), // Semi-transparent white
//                shape = RoundedCornerShape(4.dp) // Rounded corners
//            )
            .clickable {
                val intent = Intent(context, SettingsPage::class.java)
                context.startActivity(intent)
            },
        tint = Color.White
    )

}
