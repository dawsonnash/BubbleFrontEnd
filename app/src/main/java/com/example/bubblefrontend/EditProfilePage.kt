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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
                    val intent = Intent(context, ProfilePage::class.java)
                    context.startActivity(intent)
                }
            )
            Text(text = "Edit Profile", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text("Upload Profile Picture")
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
            keyboardActions = KeyboardActions(
                onDone = {
                    // Dismiss the keyboard maybe?

                    // Send the bio update request
                    val apiHandler = ApiHandler()
                    apiHandler.handleEditProfile(newBio, newName,imageURI, context)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.weight(1f))

        BottomDashboard()
    }
}


@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen()
}
