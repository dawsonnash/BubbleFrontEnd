package com.example.bubblefrontend

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.bubblefrontend.api.NonUser
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import com.google.gson.Gson

class NonUserPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userJson: String? = intent.getStringExtra("NON_USER_JSON")
        val user: NonUser? = Gson().fromJson(userJson, NonUser::class.java)
        setContent {
            BubbleFrontEndTheme {
                NonUserScreen(user)
            }
        }
    }
}

@Composable
fun NonUserScreen(user: NonUser?) {
    val context = LocalContext.current
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
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.clickable {
                            // Navigating back to the ProfilePage
                            val intent = Intent(context, UserSearchPage::class.java)
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = user?.username?: "Unknown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Read profile data from SharedPreferences
                NonUserProfileImageFollowersFollowing(imageURL = user?.profile_picture ?: "" )


                Text(
                    text = user?.name ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )

                // Bio
                Row(modifier = Modifier.padding(end = 16.dp)) {
                    Text(
                        text = user?.bio ?: "Bio is not included in JSON from server",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
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

    }

@Composable
fun NonUserProfileImageFollowersFollowing(imageURL: String) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {
        NonUserProfileIcon(imageURL, Modifier.padding(start = 16.dp, top = 16.dp))

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "0", fontWeight = FontWeight.Bold, fontSize = 20.sp) // Dynamic value for actual count
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Followers", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "0", fontWeight = FontWeight.Bold, fontSize = 20.sp) // Dynamic value for actual count
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Following", fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun NonUserProfileIcon(imageURL: String, modifier: Modifier = Modifier) {

    // Probably put this functionality in the API handler
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