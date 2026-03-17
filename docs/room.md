# Room e DAO Pattern

## Perche' c'e' un Esempio Separato

La feature TODO principale resta volutamente in-memory per non caricare troppi concetti insieme.

Per mostrare Room in modo pulito, la repo include una vertical slice separata:

- [`feature_todo_room_example/`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/)

## DAO Pattern

DAO significa `Data Access Object`.

L'idea e':

- il DAO contiene l'accesso ai dati
- le query SQL stanno in un punto dedicato
- il resto dell'app non parla direttamente con il database

## Componenti dell'Esempio

- [`RoomTodoEntity.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoEntity.kt)
  Entity di persistenza
- [`RoomTodoDao.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoDao.kt)
  DAO con query e `observeAll()`
- [`RoomTodoDatabase.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/local/RoomTodoDatabase.kt)
  Database Room
- [`RoomTodoExampleRepository.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/domain/repository/RoomTodoExampleRepository.kt)
  Contratto del domain
- [`RoomTodoExampleRepositoryImpl.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/data/repository/RoomTodoExampleRepositoryImpl.kt)
  Adapter tra DAO ed entita' di dominio
- [`ObserveAllRoomTodosUseCase.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/domain/usecase/ObserveAllRoomTodosUseCase.kt)
  Use case minimale

## `observeAll()` e Flow

Nel DAO:

```kotlin
@Query("SELECT * FROM room_todo_items ORDER BY id ASC")
fun observeAll(): Flow<List<RoomTodoEntity>>
```

Perche' e' interessante:

- espone un `Flow`
- non richiede polling manuale
- quando la tabella cambia, Room riemette il risultato

Quindi il flusso mentale e':

- cambia il database
- Room invalida la query
- il `Flow` del DAO emette il nuovo valore
- repository/use case/UI possono reagire

## Repository e Room

Nel repository dell'esempio il ruolo e' chiaro:

- il DAO sa fare query SQL
- il repository mappa `Entity` -> modello di dominio
- il domain continua a dipendere da un contratto astratto

Nota didattica:

- se il repository restituisce un `Flow` gia' fornito da Room, non serve aggiungere `flowOn(IO)` “automaticamente”

## Hilt + Room

Il wiring Hilt dell'esempio e' qui:

- [`RoomTodoExampleModule.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/di/RoomTodoExampleModule.kt)

Si vede bene la distinzione:

- `@Provides` per database e DAO
- `@Binds` per repository interface -> implementation

## Nota su KSP e Kapt

Le docs Android oggi raccomandano spesso KSP per Room nei progetti Kotlin.

Questa repo usa `kapt` anche per Room per un motivo pratico:

- Hilt nel progetto usa gia' `kapt`
- per una repo didattica compatta era piu' semplice tenere un solo setup di code generation

Il pattern DAO non cambia.
