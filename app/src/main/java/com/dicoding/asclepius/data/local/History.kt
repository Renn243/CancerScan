package com.dicoding.asclepius.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class History(
    @PrimaryKey(autoGenerate = true) var id: Int? = null,

    @ColumnInfo("image_uri") val imageUri: String,
    @ColumnInfo("label") val label: String,
    @ColumnInfo("score") val score: String,
) : Parcelable