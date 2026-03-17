package com.example.taskflow2.feature_todo_room_example.data.repository

import com.example.taskflow2.feature_todo_room_example.data.local.RoomTodoDao
import com.example.taskflow2.feature_todo_room_example.data.local.RoomTodoEntity
import com.example.taskflow2.feature_todo_room_example.data.local.toDomain
import com.example.taskflow2.feature_todo_room_example.data.local.toEntity
import com.example.taskflow2.feature_todo_room_example.domain.model.RoomTodoItem
import com.example.taskflow2.feature_todo_room_example.domain.repository.RoomTodoExampleRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository dell'esempio Room.
 *
 * Qui si vede bene la differenza tra repository e DAO:
 * - il DAO sa fare query SQL sulla tabella concreta
 * - il repository espone un contratto piu' vicino al domain
 *
 * Il repository dipende dal DAO, non dall'intero database:
 * - riduce l'accoppiamento
 * - rende piu' facile testare e sostituire la persistenza
 */
@Singleton
class RoomTodoExampleRepositoryImpl @Inject constructor(
    private val roomTodoDao: RoomTodoDao
) : RoomTodoExampleRepository {

    override fun observeAll(): Flow<List<RoomTodoItem>> {
        // Questo repository restituisce un `Flow` gia' osservabile dal DAO Room.
        // Qui ci limitiamo a mappare Entity -> Domain.
        // Nota didattica:
        // - `flowOn(IO)` ha senso soprattutto se il repository costruisce lui un
        //   `flow { ... }` con lavoro upstream esplicito
        // - con un `Flow` Room gia' pronto, non va aggiunto "per riflesso"
        return roomTodoDao.observeAll().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun add(title: String) {
        roomTodoDao.insert(
            RoomTodoEntity(
                title = title,
                completed = false
            )
        )
    }

    override suspend fun update(todo: RoomTodoItem) {
        roomTodoDao.update(todo.toEntity())
    }
}
