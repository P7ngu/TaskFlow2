package com.example.taskflow2.feature_todo_room_example.domain.repository

import com.example.taskflow2.feature_todo_room_example.domain.model.RoomTodoItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository del piccolo esempio Room.
 *
 * Il domain non conosce Room o SQL:
 * - chiede un contratto
 * - il data layer decide come usare DAO, entity e database
 */
interface RoomTodoExampleRepository {
    fun observeAll(): Flow<List<RoomTodoItem>>

    suspend fun add(title: String)

    suspend fun update(todo: RoomTodoItem)
}
