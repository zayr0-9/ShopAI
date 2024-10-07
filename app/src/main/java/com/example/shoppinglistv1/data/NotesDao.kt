package com.example.shoppinglistv1.data

import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Upsert
    suspend fun upsertNote(note: Notes) : Long

    @Delete
    suspend fun deleteNote(note: Notes)

    @Query("SELECT * FROM notes_table ORDER BY date ASC")
    fun getNotesOrderedByDate(): Flow<List<Notes>>

    @Query("SELECT * FROM notes_table ORDER BY title ASC")
    fun getNotesOrderedByQuestion(): Flow<List<Notes>>

    @Query("SELECT COUNT(id) FROM notes_table WHERE id = :noteId")
    fun noteExists(noteId: Long): Int

    @Query("SELECT * FROM notes_table WHERE id = :id")
    suspend fun getNoteById(id: Long): Notes?

    @Query("SELECT * FROM notes_table WHERE id = :noteId")
    fun getNote(noteId: Long) : Notes

    @Query("SELECT * FROM notes_table WHERE title LIKE :query")
    fun searchNotes(query: String) : Flow<List<Notes>>

}

@Dao
interface ItemDao {

    @Upsert
    suspend fun upsertItem(item : Item)

    @Delete
    suspend fun deleteItem(item : Item)

    @Query("SELECT * FROM item_table WHERE parent = :parentId")
    fun getListItemsForNote(parentId: Long) : Flow<List<Item>>

    @Query("SELECT * FROM item_table")
    fun getAllItems() : Flow<List<Item>>

    @Query("SELECT item_table.name, item_table.author, item_table.quantity, item_table.purchased, item_table.price ,notes_table.date\n" +
            "FROM item_table\n" +
            "JOIN notes_table ON item_table.parent = notes_table.id\n" +
            "WHERE item_table.name LIKE :query\n")
    fun getItemsAndDates(query:String) : Flow<List<ItemWithDate>>
}

@Dao
interface ImageDao {
    @Upsert
    suspend fun upsertImage(image : Image)

    @Delete
    suspend fun deleteImage(image : Image)

    @Query("SELECT * FROM photos_table WHERE parent = :parentId")
    fun getImageItemsForNote(parentId: Long) : Flow<List<Image>>
}