package com.dicoding.asclepius.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.dicoding.asclepius.data.local.History
import com.dicoding.asclepius.repository.HistoryRepository

class ResultViewModel(application: Application) : AndroidViewModel(application) {

    private val mHistoryRepository: HistoryRepository = HistoryRepository(application)

    fun insert(history: History) {
        mHistoryRepository.insert(history)
    }
}