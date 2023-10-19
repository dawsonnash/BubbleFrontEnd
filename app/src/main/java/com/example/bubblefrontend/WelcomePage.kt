package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class WelcomePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get SharedPreferences for AccountDetails for isLoggedIn
        val accountSharedPreferences = getSharedPreferences("AccountDetails", Context.MODE_PRIVATE)

        // Check if the user is logged in
        val isLoggedIn = accountSharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // User is logged in, navigate to the GlobalPage
            val intent = Intent(this, GlobalPage::class.java)
            startActivity(intent)
            finish() // Optional: Finish the WelcomePage so the user can't go back to it
        } else {
            // User is not logged in, continue displaying the WelcomePage
            setContent {
                BubbleFrontEndTheme {
                    WelcomeScreen()
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen() {

    val context = LocalContext.current              // For transitioning to other activities
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = {
                val intent = Intent(context, RegistrationPage::class.java) // Need to go to Registration Screen
                context.startActivity(intent)
            }
        ) {
            Text("Register")

        }
        Button(
            onClick = {
                val intent = Intent(context, LoginPage::class.java) // Need to go to login screen
                context.startActivity(intent)
            }
        ) {
            Text("Login")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun WelcomePagePreview() {
    BubbleFrontEndTheme {
        WelcomeScreen()
    }
}


