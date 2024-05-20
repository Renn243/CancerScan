package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: History)

    @Query("DELETE FROM history")
    fun deleteAll()

    @Query("SELECT * from history ORDER BY id ASC")
    fun getAllHistory(): LiveData<List<History>>
}