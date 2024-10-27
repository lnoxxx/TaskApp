package com.lnoxxdev.data.tagRepository

import com.lnoxxdev.data.R
import com.lnoxxdev.data.appDatabase.Tag
import com.lnoxxdev.data.appDatabase.TagsDao
import javax.inject.Inject

class TagRepository @Inject constructor(
    private val tagsDao: TagsDao,
) {

    val tags = tagsDao.getAll()

    suspend fun insert(name: String, colorId: Int) {
        val fixedName = name.trim(' ')
        val  tag = Tag(fixedName, colorId)
        tagsDao.insert(tag)
    }

    suspend fun delete(tag: Tag) {
        tagsDao.delete(tag)
    }

    suspend fun update(tag: Tag) {
        tagsDao.update(tag)
    }

    fun tagNameValidate(name: String): Int? {
        if (name.isEmpty()) return R.string.error_empty_name
        if (name.length < 2) return R.string.error_short_name
        if (name.length > 30) return R.string.error_long_name
        return null
    }
}