package com.example.bubblefrontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class ProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ){
                        WelcomeMessage()
                        Spacer(modifier = Modifier.weight(1f)) // This will occupy all available space
                        LogoutButton()
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeMessage() {

        Text(
            text = "Welcome to the Omniverse",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.Black
            )
        )
    }
@Composable
fun LogoutButton(){
    val context = LocalContext.current              // For transitioning to other activities
    Button(
        onClick = {

            val intent = Intent(context, WelcomePage::class.java)
            context.startActivity(intent)
        }
    ) {
        Text("Logout")
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeMessagePreview() {
    BubbleFrontEndTheme {
        WelcomeMessage()
    }
}


