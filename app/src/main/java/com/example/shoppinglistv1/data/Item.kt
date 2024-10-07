package com.example.shoppinglistv1.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.PrimaryKey



//parent column holds the id of a note
//it tells us which note an item is part of
@Entity(tableName = "item_table", foreignKeys = [
    ForeignKey(
        entity = Notes::class,
        parentColumns = ["id"],
        childColumns = ["parent"],
        onDelete = ForeignKey.CASCADE) // delete item if original note is deleted
])
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val author: String,
    val quantity: Int,
    val price : Double,
    val purchased: Boolean,
    val parent: Long // Foreign key referencing the Note table
)

