package com.example.bubblefrontend.api

import android.util.Log
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


    val isLoading = MutableLiveData<Boolean>()                  // Loading Screen when fetching is taking place
    var isFetching = MutableLiveData<Boolean>(false)       // This disables the bug to fetch multiple times in a row or at a time
    val canFetchMore = MutableLiveData(true)               // Change this wisely. It disables fetching if no more posts are left


    fun fetchPosts(page: Int, pageSize: Int) {

        isLoading.postValue(true) // Set loading to true when fetch starts
        if (isFetching.value == true || canFetchMore.value == false) return // If already fetching, dont allow. If no more posts, dont allow
        isFetching.postValue(true)

        apiService.getFeed(page, pageSize).enqueue(object : Callback<List<FeedData>> {
            override fun onResponse(call: Call<List<FeedData>>, response: Response<List<FeedData>>) {
                if (response.isSuccessful) {
                    val newFeedDataList = response.body() ?: listOf()
                    if (newFeedDataList.size < pageSize) {
                        canFetchMore.postValue(false) // No more posts to fetch
                    }
                    Log.d("FetchedPosts", "Fetch Complete - New posts.size: ${postList.value?.size ?: 0}")
                    // Append new posts to existing list
                    val currentPosts = postList.value.orEmpty()
                    postList.postValue(currentPosts + newFeedDataList)

                    // Similarly, update the UI list
                    val uiFeedDataList = newFeedDataList.map { feedData ->
                        UiFeedData(feedData)
                    }
                    _uiPostList.postValue(_uiPostList.value.orEmpty() + uiFeedDataList)

                } else {
                    handleErrorResponse(response)
                }
                isLoading.postValue(false) // Set loading to false when fetch completes
                isFetching.postValue(false)

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