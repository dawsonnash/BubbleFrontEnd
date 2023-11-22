package com.example.bubblefrontend.api

// Login API data
data class LoginRequest(
    val username: String,
    val password: String)
data class LoginResponse(val token: String)

// Registration API data
data class RegistrationRequest(
    val username: String,
    val password: String,
    val email: String,
    val name: String,
)
data class RegistrationResponse(val message: String)

// User's information
data class ProfileResponse(
    val username: String,
    val uid: Int,
    val name: String,
    val profile_picture: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val bubble_admin: Boolean,
    val email: String,
    val bio: String,
    val followers: Int,
    val following: Int,
    val created_at: String,
    val last_accessed_at: String,
    val editable: Boolean
)

data class EditProfileResponse(
    val message: String,
    val error: String?
)

// Non user data - stored as a LiveData array in NonUserModel
data class NonUser(
    val username: String,
    val uid: Int,
    val name: String,
    val profile_picture: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val bio: String
)

// Post data - stored as a LiveData array in PostModel
data class FeedData(
    val postID: Int,
    val uid: Int,
    val photo: String,
    val photo_url: String,
    val caption: String,
    val timeAgo: String,
    val likeCount: Int,
    val hasLiked: Int,
    val username: String,
    val name: String,
    val profile_picture: String,
    val url: String,
    val html_url: String
)

data class CreatePostResponse(
    val message: String,
    val error: String?
)

// Like API data
data class LikeRequestBody(
    val uid: Int,
    val postID: Int)

data class LikeResponse(
    val message: String)


