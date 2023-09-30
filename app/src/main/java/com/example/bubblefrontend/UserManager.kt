package com.example.bubblefrontend

object UserManager {

    // Initialize empty HashMap to store username and User object
    private val userMap = HashMap<String, User>()
    // Data class to hold user information
    data class User(val username: String, val password: String, val firstName: String, val lastName: String, val email: String)
    init {
        // Fake user for testing
        userMap["tomjones"] = User(username = "tomjones", password = "123", firstName = "Tom", lastName = "Jones", email = "tommyjones24@gmail.com" )
    }

    // Add a user to the HashMap
    fun addUser(username: String, password: String, firstName: String, lastName: String, email: String): Boolean {
        if (userMap.any { it.value.username == username || it.value.email == email }){
            return false // Username already exists
        }
        userMap[username] = User(username, password, firstName, lastName, email)
        return true // User added successfully
    }

    // Check if a username and password are valid
    fun isValidLogin(identifier: String, password: String): Boolean {
        // First, check if the identifier is a username in the map
        var user = userMap[identifier]

        // If not found by username, search by email
        if (user == null) {
            user = userMap.values.find { it.email == identifier }
        }

        return user?.password == password
    }
}
