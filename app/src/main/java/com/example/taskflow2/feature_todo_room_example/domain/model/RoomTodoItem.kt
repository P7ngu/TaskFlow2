package com.example.taskflow2.feature_todo_room_example.domain.model

/**
 * Modello di dominio usato dall'esempio Room separato dalla feature TODO principale.
 *
 * Tenerlo distinto aiuta a mostrare che:
 * - il domain non deve conoscere annotation Room
 * - `Entity` e modello di dominio non sono obbligati a coincidere
 */
data class RoomTodoItem(
    val id: Long,
    val title: String,
    val completed: Boolean
)
