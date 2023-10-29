package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class SettingsPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
               SettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsScreen() {

    val context = LocalContext.current              // For transitioning to other activities

    val accountSharedPreferences: SharedPreferences =
        context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
    val profileSharedPreferences: SharedPreferences =
        context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

    val storedToken = accountSharedPreferences.getString("token", "No token found") ?: "No token found"
    val storedUsername = accountSharedPreferences.getString("username", "No username found") ?: "No username found"
    val storedUID = profileSharedPreferences.getInt("uid", -1)
    val storedName = profileSharedPreferences.getString("name", "No name found") ?: "No name found"
    val storedProfilePicture = profileSharedPreferences.getString("profile_picture", "No profilePicture found") ?: "No profilePicture found"
    val storedURL = profileSharedPreferences.getString("url", "No url found") ?: "No url found"
    val storedHTMLURL = profileSharedPreferences.getString("html_url", "No html_url found") ?: "No html_url found"
    val storedFollowersURL = profileSharedPreferences.getString("followers_url", "No followers_url found") ?: "No followers_url found"
    val storedFollowingURL = profileSharedPreferences.getString("following_url", "No following_url found") ?: "No following_url found"
    val storedBubbleAdmin = profileSharedPreferences.getBoolean("bubble_admin", false)
    val storedEmail = profileSharedPreferences.getString("email", "No email found") ?: "No email found"
    val storedBio = profileSharedPreferences.getString("bio", "No bio found") ?: "No bio found"
    val storedFollowers = profileSharedPreferences.getInt("followers", -1)
    val storedFollowing = profileSharedPreferences.getInt("following", -1)
    val storedCreatedAt = profileSharedPreferences.getString("created_at", "No created_at found") ?: "No created_at found"
    val storedLastAccessedAt = profileSharedPreferences.getString("last_accessed_at", "No last_accessed_at found") ?: "No last_accessed_at found"
    val storedEditable = profileSharedPreferences.getBoolean("editable", false)



    Column{
        Text("Stored Token: $storedToken")
        Text(text = "Username: $storedUsername")
        Text(text = "UID: $storedUID")
        Text(text = "Name: $storedName")
        Text(text = "Profile Picture: $storedProfilePicture")
        Text(text = "URL: $storedURL")
        Text(text = "HTML URL: $storedHTMLURL")
        Text(text = "Followers URL: $storedFollowersURL")
        Text(text = "Following URL: $storedFollowingURL")
        Text(text = "Bubble Admin: $storedBubbleAdmin")
        Text(text = "Email: $storedEmail")
        Text(text = "Bio: $storedBio")
        Text(text = "Followers: $storedFollowers")
        Text(text = "Following: $storedFollowing")
        Text(text = "Created At: $storedCreatedAt")
        Text(text = "Last Accessed At: $storedLastAccessedAt")
        Text(text = "Editable: $storedEditable")

        Spacer(modifier = Modifier.weight(1f))
        LogoutButton(context, accountSharedPreferences)

    }
}
@Composable
fun LogoutButton(context: Context, accountSharedPreferences: SharedPreferences){
    val editor = accountSharedPreferences.edit()
    Button(
        onClick = {
            // Set isLoggedIn to false
            editor.putBoolean("isLoggedIn", false)
            editor.apply()

            val intent = Intent(context, WelcomePage::class.java)
            context.startActivity(intent)
        }
    ) {
        Text("Logout")


    }
}

