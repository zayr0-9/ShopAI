package com.example.shoppinglistv1.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Notes (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: Long,
    val content: String
)

