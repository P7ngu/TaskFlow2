package com.example.taskflow2.feature_todo_room_example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room dell'esempio DAO.
 *
 * L'entity rappresenta il formato di persistenza.
 * Il domain, invece, usa `RoomTodoItem`, che resta indipendente da Room.
 */
@Entity(tableName = "room_todo_items")
data class RoomTodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val completed: Boolean
)
