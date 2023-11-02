package com.example.bubblefrontend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

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

@Composable
fun BottomDashboard() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DashboardButton(
                title = "Global",
                icon = Icons.Default.Home,
                onClick = {
                    val intent = Intent(context, GlobalPage::class.java)
                    context.startActivity(intent)
                }
            )
            DashboardButton(
                title = "Search",
                icon = Icons.Default.Search,
                onClick = {
                    val intent = Intent(context, UserSearchPage::class.java)
                    context.startActivity(intent)
                }
            )
            DashboardButton(
                title = "Profile",
                icon = Icons.Default.AccountCircle,
                onClick = {
                    val intent = Intent(context, ProfilePage::class.java)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun DashboardButton(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(imageVector = icon, contentDescription = title)
        Text(text = title)
    }
}
