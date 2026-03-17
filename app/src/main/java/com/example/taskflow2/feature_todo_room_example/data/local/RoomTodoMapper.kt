package com.example.taskflow2.feature_todo_room_example.data.local

import com.example.taskflow2.feature_todo_room_example.domain.model.RoomTodoItem

fun RoomTodoEntity.toDomain(): RoomTodoItem {
    return RoomTodoItem(
        id = id,
        title = title,
        completed = completed
    )
}

fun RoomTodoItem.toEntity(): RoomTodoEntity {
    return RoomTodoEntity(
        id = id,
        title = title,
        completed = completed
    )
}
