package com.example.taskflow2.feature_todo_room_example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO = Data Access Object.
 *
 * Questo pattern isola le query SQL dietro un contratto dedicato:
 * - chi usa il DAO non deve conoscere dettagli del database
 * - Room genera l'implementazione concreta a compile time
 *
 * Qui `observeAll()` restituisce un `Flow`:
 * - non facciamo polling manuale
 * - Room riesegue la query ed emette un nuovo valore quando la tabella cambia
 */
@Dao
interface RoomTodoDao {

    @Query("SELECT * FROM room_todo_items ORDER BY id ASC")
    fun observeAll(): Flow<List<RoomTodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: RoomTodoEntity): Long

    @Update
    suspend fun update(todo: RoomTodoEntity)

    @Query("DELETE FROM room_todo_items")
    suspend fun clearAll()
}
