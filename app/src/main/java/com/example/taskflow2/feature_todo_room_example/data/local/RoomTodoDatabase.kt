package com.example.taskflow2.feature_todo_room_example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Il database Room espone i DAO, non query sparse nell'app.
 *
 * Regola pratica:
 * - il `Database` orchestra le tabelle disponibili
 * - il `Dao` contiene le operazioni di accesso ai dati
 */
@Database(
    entities = [RoomTodoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RoomTodoDatabase : RoomDatabase() {

    abstract fun roomTodoDao(): RoomTodoDao

    companion object {
        const val DATABASE_NAME = "taskflow2-room-example.db"
    }
}
