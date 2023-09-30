package com.example.bubblefrontend

object UserManager {

    // Initialize empty HashMap to store username and User object
    private val userMap = HashMap<String, User>()
    init {
        // Adding a "fake" user for testing
        userMap["tomjones"] = User("tomjones", "123")
    }
    // Data class to hold user information
    data class User(val username: String, val password: String)

    // Function to add a user to the HashMap
    fun addUser(username: String, password: String): Boolean {
        if (userMap.containsKey(username)) {
            return false // Username already exists
        }
        userMap[username] = User(username, password)
        return true // User added successfully
    }

    // Function to check if a username and password are valid
    fun isValidLogin(username: String, password: String): Boolean {
        val user = userMap[username]
        return user?.password == password
    }
}
