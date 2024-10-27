package com.lnoxxdev.data

import android.content.Context
import androidx.room.Room
import com.lnoxxdev.data.appDatabase.AppDatabase
import com.lnoxxdev.data.dateRepository.DateRepository
import com.lnoxxdev.data.notification.ReminderManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideDateRepository() = DateRepository()

    @Provides
    @Singleton
    fun provideNotesDao(database: AppDatabase) = database.getNotesDao()

    @Provides
    @Singleton
    fun provideTagsDao(database: AppDatabase) = database.getTagsDao()

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase) = database.getTaskDao()

    @Provides
    @Singleton
    fun provideReminderManager(@ApplicationContext context: Context) = ReminderManager(context)

    @Provides
    @Singleton
    fun provideDateTimeManager() = DateTimeManager()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "appDatabase"
        ).build()
    }
}