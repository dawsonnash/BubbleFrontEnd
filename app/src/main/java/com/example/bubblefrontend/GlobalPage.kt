package com.example.bubblefrontend

import kotlin.math.sqrt
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.bubblefrontend.ui.theme.BubbleFrontEndTheme

class GlobalPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BubbleFrontEndTheme {
                GlobalScreen()
            }
        }
    }
}


data class Post(
    val text: String?,
    val pictureUrl: String? // Assuming pictures are represented by URLs
    //Example: Post(text = "Some text", pictureUrl = "http://example.com/picture.jpg")
    //Example: Post(text = null, pictureUrl = "http://example.com/picture.jpg")
)

fun initializePosts(): List<Post> {
    val posts = mutableListOf<Post>()
    for (i in 1..625) {
        posts.add(Post(text = "Initialization Post $i", pictureUrl = null)) // Adding a number for differentiation
    }
    return posts
}

@Composable
fun FullScreenPostView(post: Post, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            Button(onClick = onBack) {
                Text("Back")
            }

            Text(
                text = post.text ?: "",
                fontSize = 20.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )

            // Only display the image if there is a picture URL
            post.pictureUrl?.let {
                Image(
                    painter = painterResource(id = R.drawable.profile_icon), // Replace with your actual image resource
                    contentDescription = "Post Image",
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}


@Composable
fun GlobalScreen() {
    val posts = remember { mutableStateListOf(*initializePosts().toTypedArray()) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    if (selectedPost != null) {
        // Render the full screen view for the selected post
        FullScreenPostView(post = selectedPost!!, onBack = { selectedPost = null })
    } else {
        // Render the grid of posts
        val configuration = LocalConfiguration.current
        val density = LocalDensity.current
        val bubbleSizePx = with(density) { 400.dp.toPx() }
        val bubbleRadiusPx = bubbleSizePx / 1.25f
        val verticalDistancePx = bubbleRadiusPx * sqrt(3f) * 3 / 4
        val horizontalDistancePx = bubbleRadiusPx * 2 * 0.75f
        val columns = 25
        val totalGridWidthPx = columns * horizontalDistancePx
        val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
        val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
        val maxHorizontalScrollPx = totalGridWidthPx - (1.2f * screenWidthPx) + (horizontalDistancePx / 2)
        val maxVerticalScrollPx = (totalGridWidthPx - (2 * screenHeightPx) + (horizontalDistancePx / 2)) / 1.5f  //2.37 for 2500 posts  2.00 for 625 posts
        val initialOffsetX = -maxHorizontalScrollPx / 2
        val initialOffsetY = -maxVerticalScrollPx / 2
        var offsetX by remember { mutableStateOf(initialOffsetX) }
        var offsetY by remember { mutableStateOf(initialOffsetY) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, _, _ ->
                        val newOffsetX = (offsetX + pan.x).coerceIn(-maxHorizontalScrollPx, 0f)
                        val newOffsetY = (offsetY + pan.y).coerceIn(-maxVerticalScrollPx, 0f)
                        offsetX = newOffsetX
                        offsetY = newOffsetY
                    }
                }
                .graphicsLayer(
                    translationX = offsetX,
                    translationY = offsetY
                )
        ) {
            posts.forEachIndexed { index, post ->
                val col = index % columns
                val row = index / columns
                val xOffset = if (row % 2 == 0) col * horizontalDistancePx else col * horizontalDistancePx + horizontalDistancePx / 2
                val yOffset = row * verticalDistancePx * 3 / 4

                Box(
                    modifier = Modifier
                        .offset(x = with(density) { xOffset.toDp() }, y = with(density) { yOffset.toDp() })
                        .size(400.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.Magenta, Color.Cyan, Color.Yellow, Color.Magenta),
                                center = Offset.Zero,
                                radius = bubbleRadiusPx
                            ),
                            shape = CircleShape
                        )
                        .padding(16.dp)
                        .clickable {
                            selectedPost = post // Update the state to the clicked post
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.text ?: "",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun BottomDashboard() {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
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
                    // Update with the correct class name for UserSearchPage
                    val intent = Intent(context, UserSearchPage::class.java)
                    context.startActivity(intent)
                }
            )
            DashboardButton(
                title = "Profile",
                icon = Icons.Default.AccountCircle,
                onClick = {
                    // Update with the correct class name for ProfilePage
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
