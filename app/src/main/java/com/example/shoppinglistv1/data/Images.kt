package com.example.shoppinglistv1.data

import android.net.Uri
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "photos_table", foreignKeys = [
    ForeignKey(
        entity = Notes::class,
        parentColumns = ["id"],
        childColumns = ["parent"],
        onDelete = ForeignKey.CASCADE) // delete item if original note is deleted
])
data class Image(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val parent: Long// Foreign key referencing the Note table
)
