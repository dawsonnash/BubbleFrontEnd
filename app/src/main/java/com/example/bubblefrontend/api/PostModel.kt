package com.example.bubblefrontend.api

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostModel : ViewModel() {

    // LiveData to store a list of posts
    val postList = MutableLiveData<List<FeedData>>()

    val toastMessage = MutableLiveData<String>()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://54.202.77.126:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: ApiMethods = retrofit.create(ApiMethods::class.java)

    fun fetchPosts(page: Int, pageSize: Int) {
        apiService.getFeed(page, pageSize).enqueue(object : Callback<List<FeedData>> {
            override fun onResponse(call: Call<List<FeedData>>, response: Response<List<FeedData>>) {
                if (response.isSuccessful) {
                    postList.postValue(response.body())
                    Log.d("PostModel", "Posts fetched: ${response.body()}")
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<List<FeedData>>, t: Throwable) {
                toastMessage.postValue("Network error, bruh!")
                Log.e("PostViewModel", "Network error", t)
            }
        })
    }

    private fun handleErrorResponse(response: Response<List<FeedData>>) {
        when (response.code()) {
            400 -> toastMessage.postValue("Bad Request")
            500 -> toastMessage.postValue("Internal Server Error")
            else -> toastMessage.postValue("Error: ${response.code()}")
        }
        Log.e("PostViewModel", "Failed to fetch posts. Error code: ${response.code()}")
    }
}