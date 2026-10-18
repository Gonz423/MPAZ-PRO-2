package com.example.appmenu.room

import kotlinx.coroutines.flow.Flow
import kotlin.text.insert

class NoteRepository(private val dao: NoteDao) {
    val notes: Flow<List<Note>> = dao.observeAll()

    suspend fun save(title: String, content: String) {
        dao.insert(Note(title = title.trim(), content = content.trim()))
    }

    suspend fun delete(note: Note) {
        dao.delete(note)
    }
}
