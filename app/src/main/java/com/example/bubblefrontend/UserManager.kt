package com.example.bubblefrontend

object UserManager {

    // Initialize empty HashMap to store username and User object
    private val userMap = HashMap<String, User>()
    // Data class to hold user information
    data class User(val username: String, val password: String, val firstName: String, val lastName: String, val email: String) {
    }

    init {
        // Fake user for testing
        userMap["bubbleadmin"] = User(username = "bubbleAdmin", password = "123", firstName = "Bit", lastName = "Benders", email = "bitbenders@bubble.com" )
        userMap["dawsonnash"] = User(username = "dawsonnash", password = "123", firstName = "Dawson", lastName = "Nash", email = "dawsonnash@bubble.com" )
        userMap["issacvinson"] = User(username = "issacvinson", password = "123", firstName = "Issac", lastName = "Vinson", email = "issac@bubble.com" )
        userMap["benbrown"] = User(username = "benbrown", password = "123", firstName = "Ben", lastName = "Brown", email = "benbrown@bubble.com" )
        userMap["kaiiiverson"] = User(username = "kaiiiverson", password = "123", firstName = "Kai", lastName = "Iverson", email = "kaiiiverson@bubble.com" )


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

    // Temporary functions to display registered accounts
    fun getUserDetails(username: String): User? {
        return userMap[username]
    }
    fun getAllUsernames(): List<String> {
        return userMap.keys.toList()
    }



}
