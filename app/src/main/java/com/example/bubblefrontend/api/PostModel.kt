package com.example.bubblefrontend.api

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory



class PostModel() : ViewModel() {

    // LiveData to store a list of posts
    val postList = MutableLiveData<List<FeedData>>()

    private val _uiPostList = MutableLiveData<List<UiFeedData>>()
    val uiPostList: LiveData<List<UiFeedData>> = _uiPostList

    val toastMessage = MutableLiveData<String>()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://54.202.77.126:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private val apiService: ApiMethods = retrofit.create(ApiMethods::class.java)

    fun fetchPosts(page: Int, pageSize: Int, uid: Int) {
        Log.d("ThisFetchPosts", " page: $page, pageSize: $pageSize, $uid")
        apiService.getFeed(page, pageSize, uid).enqueue(object : Callback<List<FeedData>> {
            override fun onResponse(call: Call<List<FeedData>>, response: Response<List<FeedData>>) {
                if (response.isSuccessful)  {
                    val feedDataList = response.body() ?: listOf()
                    postList.postValue(feedDataList)

                    val uiFeedDataList = feedDataList.map { feedData ->
                        UiFeedData(feedData)
                    }
                    _uiPostList.postValue(uiFeedDataList)
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