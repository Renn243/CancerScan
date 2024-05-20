package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.local.History
import com.dicoding.asclepius.repository.HistoryRepository

class HistoryViewModel(application: Application) : ViewModel() {

    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)

    private val _history = MutableLiveData<List<History>>()
    val history: LiveData<List<History>> = _history

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isEmpty = MutableLiveData<Boolean>()
    val isEmpty: LiveData<Boolean> = _isEmpty

    fun getAllHistory() {
        _isLoading.value = true
        mHistoryRepository.getAllHistory().observeForever { historyList ->
            _isLoading.value = false
        }
    }

    fun deleteAllHistory() {
        mHistoryRepository.deleteAllHistory()
    }

    init {
        loadResults()
    }

    private fun loadResults() {
        mHistoryRepository.getAllHistory().observeForever {
            _history.postValue(it)
        }
    }
}