# TaskFlow2

Progetto Android didattico in Kotlin pensato per studiare, con esempi concreti, questi temi:

- MVVM
- Clean Architecture
- Unidirectional Data Flow
- Hilt
- Retrofit
- Room
- Coroutine e Flow
- testing di use case, ViewModel e stream asincroni

L'app contiene due feature reali:

- `feature_todo`: feature principale con TODO list, DI manuale e flusso UDF
- `feature_home`: feature dimostrativa con Hilt, Retrofit, cache locale e repository

In piu' ci sono due aree di supporto:

- `feature_todo_room_example`: esempio separato di DAO + Room
- `samples/`: helper esplicitamente didattici per Hilt e coroutine

## Quick Start

### Android Studio

1. Clona o scarica la repo
2. Apri la cartella `TaskFlow2` in Android Studio
3. Attendi il sync Gradle iniziale
4. Avvia un emulatore o collega un device
5. Premi `Run` sul modulo `app`

### Da terminale

Build debug:

```bash
./gradlew assembleDebug
```

Test unitari:

```bash
./gradlew test
```

APK debug generato:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Nota:

- `local.properties` non va condiviso
- al primo avvio Gradle puo' scaricare dipendenze e richiedere un po' di tempo

## Documentazione

La documentazione lunga e' stata organizzata in stile wiki dentro `docs/`.

- [Indice documentazione](docs/README.md)
- [Getting Started](docs/getting-started.md)
- [Architettura](docs/architecture.md)
- [Coroutine e Flow](docs/coroutines-and-flow.md)
- [Hilt e DI](docs/hilt.md)
- [Room e DAO Pattern](docs/room.md)
- [Testing Guide](docs/testing.md)

## Mappa Rapida

- `app/src/main/java/com/example/taskflow2/feature_todo/`
  Feature TODO principale
- `app/src/main/java/com/example/taskflow2/feature_home/`
  Feature Home con Hilt + Retrofit
- `app/src/main/java/com/example/taskflow2/feature_todo_room_example/`
  Esempio Room separato
- `app/src/main/java/com/example/taskflow2/samples/`
  Sample helper per concetti didattici isolati
- `app/src/test/java/com/example/taskflow2/`
  Test per architettura, coroutine, Flow e data layer

## Percorso Consigliato

Se vuoi capire il progetto velocemente:

1. Leggi [Getting Started](docs/getting-started.md)
2. Leggi [Architettura](docs/architecture.md)
3. Guarda `feature_todo`
4. Guarda [Testing Guide](docs/testing.md)
5. Approfondisci [Coroutine e Flow](docs/coroutines-and-flow.md) e [Hilt e DI](docs/hilt.md)

## Licenza

Questa repo usa una licenza personalizzata di tipo "learning-only".

In breve:

- puoi usarla, studiarla e modificarla per imparare
- non puoi usarla per insegnare, fare corsi, workshop, tutoring o contenuti formativi derivati

Testo completo in [LICENSE](LICENSE).
