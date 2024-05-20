package com.dicoding.asclepius.view

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.remote.response.HealthNewsResponse
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

    private val _newsList = MutableLiveData<HealthNewsResponse>()
    val newsList: LiveData<HealthNewsResponse> = _newsList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getNews()
    }

    fun getNews() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getNews()
        client.enqueue(object : Callback<HealthNewsResponse> {
            override fun onResponse(
                call: Call<HealthNewsResponse>,
                response: Response<HealthNewsResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        _newsList.value = it
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<HealthNewsResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
            }
        })
    }

    companion object{
        private const val TAG = "MainViewModel"
    }
}