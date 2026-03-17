# Hilt e Dependency Injection

## Perche' MVVM + DI Funzionano Bene Insieme

MVVM e DI si rafforzano a vicenda:

- il ViewModel resta focalizzato su stato ed eventi
- la business logic vive in use case e repository
- le dipendenze concrete si sostituiscono facilmente nei test
- il wiring resta fuori dalla UI

Nel progetto questo si vede bene in:

- [`feature_todo/`](../app/src/main/java/com/example/taskflow2/feature_todo/)
- [`feature_home/`](../app/src/main/java/com/example/taskflow2/feature_home/)
- [`app/src/test/java/com/example/taskflow2/`](../app/src/test/java/com/example/taskflow2/)

## `@Inject`, `@Binds`, `@Provides`

### `@Inject`

Serve a dire a Hilt come costruire una classe tramite costruttore.

### `@Binds`

Usalo per collegare:

- interfaccia -> implementazione

quando l'implementazione ha gia' un costruttore `@Inject`.

Esempio:

- [`HomeFeatureModule.kt`](../app/src/main/java/com/example/taskflow2/feature_home/di/HomeFeatureModule.kt)

### `@Provides`

Usalo quando un oggetto va costruito manualmente:

- Retrofit
- OkHttp
- builder/factory esterni
- Room database

Esempi:

- [`NetworkModule.kt`](../app/src/main/java/com/example/taskflow2/core/di/NetworkModule.kt)
- [`RoomTodoExampleModule.kt`](../app/src/main/java/com/example/taskflow2/feature_todo_room_example/di/RoomTodoExampleModule.kt)

## Scope

### `@Singleton`

Vive per il ciclo di vita del processo app.

Usalo per:

- Retrofit
- OkHttp
- repository condivisi
- helper application-wide

### `@ActivityScoped`

Vive quanto una singola Activity.

Usalo quando una dipendenza usa `@ActivityContext` o appartiene davvero alla schermata.

Esempio:

- [`samples/hilt/ActivitySessionTracker.kt`](../app/src/main/java/com/example/taskflow2/samples/hilt/ActivitySessionTracker.kt)

### `@ViewModelScoped`

Vive quanto un singolo ViewModel.

Utile per:

- stato tecnico temporaneo del ViewModel
- coordinatori che non devono diventare globali

Esempio:

- [`samples/hilt/HomeRefreshSession.kt`](../app/src/main/java/com/example/taskflow2/samples/hilt/HomeRefreshSession.kt)

## Qualifier

I qualifier servono quando hai piu' dipendenze dello stesso tipo.

### Built-in

- `@ApplicationContext`
- `@ActivityContext`

### Custom

- `@AppPackageName`
- `@TeachingBaseUrl`
- `@TeachingHttpClient`

File chiave:

- [`AppQualifiers.kt`](../app/src/main/java/com/example/taskflow2/core/di/AppQualifiers.kt)
- [`NetworkQualifiers.kt`](../app/src/main/java/com/example/taskflow2/core/di/NetworkQualifiers.kt)
- [`samples/hilt/AppIdentityProvider.kt`](../app/src/main/java/com/example/taskflow2/samples/hilt/AppIdentityProvider.kt)

## Anti-Leak

Regola fondamentale:

- non mettere in `@Singleton` oggetti che trattengono `Activity`, `Fragment`, `View` o `@ActivityContext`

Questo evita:

- memory leak
- state leak tra schermate o ViewModel diversi

## Stato Attuale del Progetto

Il progetto usa due approcci di proposito:

- `feature_home` usa Hilt
- `feature_todo` usa DI manuale

Questa asimmetria e' intenzionale: serve a mostrare confronto tra wiring manuale e DI moderna.

## Dove Guardare nel Codice

- [`TaskFlowApplication.kt`](../app/src/main/java/com/example/taskflow2/TaskFlowApplication.kt)
- [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
- [`NetworkModule.kt`](../app/src/main/java/com/example/taskflow2/core/di/NetworkModule.kt)
- [`HomeFeatureModule.kt`](../app/src/main/java/com/example/taskflow2/feature_home/di/HomeFeatureModule.kt)
- [`samples/hilt/`](../app/src/main/java/com/example/taskflow2/samples/hilt/)
