package com.lnoxxdev.data.notesRepository

import com.lnoxxdev.data.appDatabase.Note
import com.lnoxxdev.data.appDatabase.NotesDao
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDao: NotesDao
) {

    val notesFlow = notesDao.getAll()

    suspend fun insert(note: Note) {
        notesDao.insert(note)
    }

    suspend fun update(note: Note) {
        notesDao.update(note)
    }

    suspend fun delete(note: Note) {
        notesDao.delete(note)
    }
}