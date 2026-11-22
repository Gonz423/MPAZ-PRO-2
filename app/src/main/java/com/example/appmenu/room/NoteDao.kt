package com.example.appmenu.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Note>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)
    @Delete
    suspend fun delete(note: Note)
}
