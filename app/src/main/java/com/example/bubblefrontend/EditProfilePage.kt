package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bubblefrontend.api.ApiHandler
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class EditProfilePage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                EditProfileScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen() {
    val context = LocalContext.current
    val profileSharedPreferences = context.getSharedPreferences("ProfileData", Context.MODE_PRIVATE)

    var newBio by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }
    var imageURI by remember { mutableStateOf<Uri?>(null) }  // <-- add this line

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
    // Launcher to handle the result from the image picker - like whatever image you choose
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned URI (i.e. store it to the state)
        if (uri != null) {
            imageURI = uri  // <-- store the URI
            Toast.makeText(context, "Selected image URI: $uri", Toast.LENGTH_LONG).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
            Text(text = "Edit Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Edit Name") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()

        )
        OutlinedTextField(
            value = newBio,
            onValueChange = { newBio = it },
            label = { Text("Edit Bio") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            launcher.launch("image/*")
        },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush, shape = RoundedCornerShape(32.dp))
                .padding(8.dp)
        ) {
            Text("Upload Profile Picture",  color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = {
            val apiHandler = ApiHandler()
            apiHandler.handleEditProfile(newBio, newName,imageURI, context)},
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush, shape = RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            Text("Save Changes",  color = Color.White)
        }


        BottomDashboard()
    }
}


@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}
