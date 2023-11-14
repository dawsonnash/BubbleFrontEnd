package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme
import kotlin.random.Random

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

    // Bubble-like gradient brush
    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF856AB8), // Lighter dark purple
            Color(0xFF5E7AB3), // Lighter dark periwinkle/blue
            Color(0xFF7897AB)  // Lighter dark blue/grey
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    )
    val context = LocalContext.current              // For transitioning to other activities
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Set the image as the background
        Image(
            painter = painterResource(id = R.drawable.bubblewelcomepage),
            contentDescription = "Background",
            modifier = Modifier.matchParentSize(), // This will make the image match the size of the Box
            contentScale = ContentScale.Crop // This will crop the image if necessary to fill the Box
        )
        //RisingBubbleAnimation()
        Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {

            Button(
                onClick = {
                    val intent = Intent(
                        context,
                        RegistrationPage::class.java
                    ) // Need to go to Registration Screen
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .background(gradientBrush, shape = RoundedCornerShape(32.dp))
            ) {
                Text("Register", color = Color.White)

            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    val intent =
                        Intent(context, LoginPage::class.java) // Need to go to login screen
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
                    .background(gradientBrush, shape = RoundedCornerShape(32.dp))
            ) {
                Text("Login", color = Color.White)
            }
        }

    }
}

@Composable
fun RisingBubbleAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val state = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing), // Slower rise
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    // Create a list of bubbles with random starting x-coordinates and sizes
    val bubbles = remember { List(15) { // Increase or decrease number of bubbles as needed
        Bubble(
            Random.nextFloat() * 800f, // Random x-coordinate, assuming max screen width around 800 pixels
            Random.nextFloat() * 100f + 20f, // Random size, minimum size of 20f
        )
    }}

    Canvas(modifier = Modifier.fillMaxSize()) {
        bubbles.forEach { bubble ->
            val yOffset = state.value * size.height - bubble.size // Start from below the screen
            drawCircle(
                color = Color(0xFF856AB8).copy(alpha = 0.1f), // Slight purple tint with transparency
                radius = bubble.size,
                center = Offset(bubble.x, yOffset)
            )
        }
    }
}

data class Bubble(val x: Float, val size: Float)



@Preview(showBackground = true)
@Composable
fun WelcomePagePreview() {
    BubbleFrontEndTheme {
        WelcomeScreen()
    }
}


