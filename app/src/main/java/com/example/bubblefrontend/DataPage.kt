package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class DataPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                DataPageScreen()
            }
        }
    }
}


@Composable
fun DataPageScreen() {

    val context = LocalContext.current              // For transitioning to other activities

    val accountSharedPreferences: SharedPreferences =
        context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
    val profileSharedPreferences: SharedPreferences =
        context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

    val storedToken =
        accountSharedPreferences.getString("token", "No token found") ?: "No token found"
    val storedUsername =
        accountSharedPreferences.getString("username", "No username found") ?: "No username found"
    val storedUID = profileSharedPreferences.getInt("uid", -1)
    val storedName = profileSharedPreferences.getString("name", "No name found") ?: "No name found"
    val storedProfilePicture =
        profileSharedPreferences.getString("profile_picture", "No profilePicture found")
            ?: "No profilePicture found"
    val storedURL = profileSharedPreferences.getString("url", "No url found") ?: "No url found"
    val storedHTMLURL =
        profileSharedPreferences.getString("html_url", "No html_url found") ?: "No html_url found"
    val storedFollowersURL =
        profileSharedPreferences.getString("followers_url", "No followers_url found")
            ?: "No followers_url found"
    val storedFollowingURL =
        profileSharedPreferences.getString("following_url", "No following_url found")
            ?: "No following_url found"
    val storedBubbleAdmin = profileSharedPreferences.getBoolean("bubble_admin", false)
    val storedEmail =
        profileSharedPreferences.getString("email", "No email found") ?: "No email found"
    val storedBio = profileSharedPreferences.getString("bio", "No bio found") ?: "No bio found"
    val storedFollowers = profileSharedPreferences.getInt("followers", -1)
    val storedFollowing = profileSharedPreferences.getInt("following", -1)
    val storedCreatedAt = profileSharedPreferences.getString("created_at", "No created_at found")
        ?: "No created_at found"
    val storedLastAccessedAt =
        profileSharedPreferences.getString("last_accessed_at", "No last_accessed_at found")
            ?: "No last_accessed_at found"
    val storedEditable = profileSharedPreferences.getBoolean("editable", false)

    // Create a list of label-value pairs
    val userDataList = listOf(
        "Token" to storedToken,
        "Username" to storedUsername,
        "UID" to storedUID.toString(),
        "Name" to storedName,
        "Profile Picture" to storedProfilePicture,
        "URL" to storedURL,
        "HTML URL" to storedHTMLURL,
        "Followers URL" to storedFollowersURL,
        "Following URL" to storedFollowingURL,
        "Bubble Admin" to storedBubbleAdmin.toString(),
        "Bio" to storedBio,
        "Email" to storedEmail,
        "Followers" to storedFollowers.toString(),
        "Following" to storedFollowing.toString(),
        "Created At" to storedCreatedAt,
        "Last Accessed At" to storedLastAccessedAt,
        "Editable" to storedEditable.toString(),
    )

    Column() {

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
                    val intent = Intent(context, SettingsPage::class.java)
                    context.startActivity(intent)
                }
            )
            Text(text = "User Data", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Display each data item
            items(userDataList) { dataItem ->
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("${dataItem.first}: ")
                        }
                        append(dataItem.second)
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Divider()
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        LogoutButton(context, accountSharedPreferences)
    }
}

