package com.example.taskflow2.feature_todo_room_example.di

import android.content.Context
import androidx.room.Room
import com.example.taskflow2.feature_todo_room_example.data.local.RoomTodoDao
import com.example.taskflow2.feature_todo_room_example.data.local.RoomTodoDatabase
import com.example.taskflow2.feature_todo_room_example.data.repository.RoomTodoExampleRepositoryImpl
import com.example.taskflow2.feature_todo_room_example.domain.repository.RoomTodoExampleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo Hilt dell'esempio Room tenuto separato dal resto della feature TODO.
 *
 * Qui usiamo `@Provides` per creare il database via builder Room e il DAO
 * ottenuto dal database. Usiamo `@Binds` per collegare interfaccia repository
 * e implementazione concreta, perche' l'implementazione ha gia' `@Inject`.
 */
@Module
@InstallIn(SingletonComponent::class)
object RoomTodoExampleDatabaseModule {

    @Provides
    @Singleton
    fun provideRoomTodoDatabase(
        @ApplicationContext applicationContext: Context
    ): RoomTodoDatabase {
        return Room.databaseBuilder(
            applicationContext,
            RoomTodoDatabase::class.java,
            RoomTodoDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    fun provideRoomTodoDao(
        database: RoomTodoDatabase
    ): RoomTodoDao {
        return database.roomTodoDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RoomTodoExampleBindingModule {

    @Binds
    @Singleton
    abstract fun bindRoomTodoExampleRepository(
        implementation: RoomTodoExampleRepositoryImpl
    ): RoomTodoExampleRepository
}
