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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    var storedToken by remember { mutableStateOf("") }
    var storedUsername by remember { mutableStateOf("") }
    var storedName by remember { mutableStateOf("") }
    var storedBio by remember { mutableStateOf("") }
    var storedProfilePicture by remember { mutableStateOf("") }
    var storedAccountCreated by remember { mutableStateOf("") }
    var storedEditable by remember { mutableStateOf(false) }


    val accountSharedPreferences: SharedPreferences =
        context.getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)
    val profileSharedPreferences: SharedPreferences =
        context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)
    storedToken = accountSharedPreferences.getString("token", "No token found") ?: "No token found"
    storedUsername = accountSharedPreferences.getString("username", "No username found") ?: "No username found"
    storedName = profileSharedPreferences.getString("name", "No name found") ?: "No name found"
    storedBio = profileSharedPreferences.getString("bio", "No bio found") ?: "No bio found"
    storedProfilePicture = profileSharedPreferences.getString("profilePicture", "No profilePicture found") ?: "No profilePicture found"
    storedAccountCreated = profileSharedPreferences.getString("accountCreated", "No accountCreated found") ?: "No accountCreated found"
    storedEditable = profileSharedPreferences.getBoolean("editable", false)



    Column() {
        Text("Stored Token: $storedToken")
        Text(text = "Username: $storedUsername")
        Text(text = "Name: $storedName")
        Text(text = "Bio: $storedBio")
        Text(text = "Profile Picture: $storedProfilePicture")
        Text(text = "Account Created: $storedAccountCreated")
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

