package com.dicoding.asclepius.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.data.local.History
import com.dicoding.asclepius.data.local.HistoryDao
import com.dicoding.asclepius.data.local.HistoryDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {

    private val mHistoryDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDatabase.getDatabase(application)
        mHistoryDao = db.historyDao()
    }

    fun insert(history: History) {
        executorService.execute { mHistoryDao.insert(history) }
    }

    fun deleteAllHistory() {
        executorService.execute { mHistoryDao.deleteAll() }
    }

    fun getAllHistory(): LiveData<List<History>> = mHistoryDao.getAllHistory()
}