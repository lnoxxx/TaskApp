package com.lnoxxdev.data.tagRepository

import com.lnoxxdev.data.appDatabase.Tag
import com.lnoxxdev.data.appDatabase.TagsDao
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val tagsDao: TagsDao,
) {

    val tags = tagsDao.getAll()

    suspend fun insert(tag: Tag) {
        tagsDao.insert(tag)
    }

    suspend fun delete(tag: Tag) {
        tagsDao.delete(tag)
    }

    suspend fun update(tag: Tag) {
        tagsDao.update(tag)
    }

}