package com.example.bubblefrontend.api
import android.content.Context
import android.content.SharedPreferences

class RefreshToken(private val context: Context) {

    private val accountSharedPreferences: SharedPreferences = context.getSharedPreferences("AccountData", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = accountSharedPreferences.edit()

    val username = accountSharedPreferences.getString("username", "")
    private val password = accountSharedPreferences.getString("password", "")

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
        if (key == "editable") {
            val isEditable = prefs.getBoolean("editable", false)
            if (!isEditable) {
                // If token is expired - refresh it
                if (username != null && password != null) {
                    refreshToken(username, password, editor)
                }
            }
        }
    }

    init {
        accountSharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    fun unregisterPreferenceChangeListener() {
        accountSharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    private fun refreshToken(username: String, password: String, editor: SharedPreferences.Editor) {
        val apiHandler = ApiHandler()
        apiHandler.handleLogin(username, password, context, editor)
    }
}
