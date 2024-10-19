package com.lnoxxdev.data.appDatabase

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//notes

@Dao
interface NotesDao {
    @Insert
    suspend fun insert(note: Note)

    @Query("SELECT * FROM notes")
    fun getAll(): Flow<List<Note>>

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)
}

// tasks

@Dao
interface TagsDao {
    @Insert
    suspend fun insert(tag: Tag)

    @Query("SELECT * FROM tags")
    fun getAll(): Flow<List<Tag>>

    @Delete
    suspend fun delete(tag: Tag)

    @Update
    suspend fun update(tag: Tag)
}

@Dao
interface TasksDao {
    @Insert
    suspend fun insert(task: Task)

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE " +
            "(year > :startYear OR (year = :startYear AND (month > :startMonth OR " +
            "(month = :startMonth AND day >= :startDay)))) AND (year < :endYear OR " +
            "(year = :endYear AND (month < :endMonth OR (month = :endMonth AND day <= :endDay))))")
    fun getInDateRange(
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int,
    ): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)
}

