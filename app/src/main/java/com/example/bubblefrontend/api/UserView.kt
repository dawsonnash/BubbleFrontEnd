package com.example.bubblefrontend.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserViewModel : ViewModel() {

    // Live list to store all users gathered for search query
    val userList = MutableLiveData<List<NonUser>>()

    val toastMessage = MutableLiveData<String>()

    private val retrofit: Retrofit? = Retrofit.Builder()
        .baseUrl("http://54.202.77.126:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiMethods? = retrofit?.create(ApiMethods::class.java)

    fun fetchUsers() {

        apiService?.getAllUsers()?.enqueue(object : Callback<List<NonUser>> {
            override fun onResponse(call: Call<List<NonUser>>, response: Response<List<NonUser>>) {
                if (response.isSuccessful) {
                    userList.postValue(response.body())
                    Log.d("UserViewModel", "Users fetched: ${response.body()}")  // Log response body
                }
                else {
                    when (response.code()) {
                        400 -> toastMessage.postValue("Search term not provided")
                        500 -> toastMessage.postValue("Internal server error")
                        else -> toastMessage.postValue("Error: ${response.code()}")
                    }
                    Log.e("UserViewModel", "Failed to fetch users. Error code: ${response.code()}") // Log error code

                }
            }

            override fun onFailure(call: Call<List<NonUser>>, t: Throwable) {
                toastMessage.postValue("Network error, bruh!")
                Log.e("UserViewModel", "Network error", t) // Log throwable

            }
        })
    }
}
