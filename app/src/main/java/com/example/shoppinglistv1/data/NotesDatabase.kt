package com.example.shoppinglistv1.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


//contains both database tables for notes and items
@Database(
    entities = [Notes::class, Item::class, Image::class],
    version = 7
)
@TypeConverters(UriTypeConverter::class)
abstract class NotesDatabase: RoomDatabase() {

    abstract val daoNotes: NotesDao
    abstract val daoItem: ItemDao
    abstract val daoImg: ImageDao
}