package com.lnoxxdev.data.appDatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class, Tag::class, Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getNotesDao(): NotesDao
    abstract fun getTagsDao(): TagsDao
    abstract fun getTaskDao(): TasksDao
}