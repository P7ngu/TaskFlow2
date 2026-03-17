package com.example.taskflow2.feature_todo_room_example.domain.usecase

import com.example.taskflow2.feature_todo_room_example.domain.model.RoomTodoItem
import com.example.taskflow2.feature_todo_room_example.domain.repository.RoomTodoExampleRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Use case minimale per ribadire che il domain consuma un repository astratto,
 * anche quando sotto c'e' Room con un DAO concreto.
 */
class ObserveAllRoomTodosUseCase @Inject constructor(
    private val repository: RoomTodoExampleRepository
) {
    operator fun invoke(): Flow<List<RoomTodoItem>> = repository.observeAll()
}
