# TaskFlow2

Progetto Android didattico in Kotlin pensato per studiare, con esempi concreti:

- MVVM
- Clean Architecture
- Unidirectional Data Flow
- Hilt
- Retrofit
- Room
- Coroutine e Flow
- testing di use case, ViewModel e stream asincroni

Se preferisci tutto in un file: [guida completa](docs/all-in-one.md)

## Quick Start

### Android Studio

1. Clona o scarica la repo
2. Apri la cartella `TaskFlow2` in Android Studio
3. Attendi il sync Gradle iniziale
4. Avvia un emulatore o collega un device
5. Premi `Run` sul modulo `app`

### GitHub Desktop

Se preferisci clonare il progetto con GitHub Desktop:

1. Apri GitHub Desktop
2. Vai su `File > Clone repository`
3. Incolla l'URL della repo nella tab `URL`
4. Scegli la cartella locale in cui salvarla
5. Premi `Clone`
6. Da GitHub Desktop usa `Repository > Open in Android Studio` oppure apri manualmente la cartella clonata in Android Studio

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

Note utili:

- `local.properties` non va condiviso
- al primo avvio Gradle puo' scaricare dipendenze e impiegare un po' di tempo

## Struttura Rapida

Feature reali:

- [`feature_todo`](app/src/main/java/com/example/taskflow2/feature_todo/): TODO list principale, DI manuale e flusso UDF
- [`feature_home`](app/src/main/java/com/example/taskflow2/feature_home/): feature demo con Hilt, Retrofit, cache locale e repository
- [`feature_example`](app/src/main/java/com/example/taskflow2/feature_example/): esempio minimale di pagina finale con contenuto statico

Aree di supporto:

- [`feature_todo_room_example`](app/src/main/java/com/example/taskflow2/feature_todo_room_example/): esempio separato di DAO + Room
- [`samples/`](app/src/main/java/com/example/taskflow2/samples/): helper esplicitamente didattici per Hilt e coroutine
- [`app/src/test/java/com/example/taskflow2/`](app/src/test/java/com/example/taskflow2/): test per architettura, coroutine, Flow e data layer

## Documentazione

La documentazione lunga e' stata organizzata in stile wiki dentro `docs/`.

- [Indice documentazione](docs/README.md)
- [Guida Completa](docs/all-in-one.md)
- [Getting Started](docs/getting-started.md)
- [Architettura](docs/architecture.md)
- [Coroutine e Flow](docs/coroutines-and-flow.md)
- [Hilt e DI](docs/hilt.md)
- [Room e DAO Pattern](docs/room.md)
- [Testing Guide](docs/testing.md)
- [Estendere l'app](docs/extending-app.md)

## Percorso Consigliato

Se vuoi capire il progetto velocemente:

1. Leggi [Getting Started](docs/getting-started.md)
2. Leggi [Architettura](docs/architecture.md)
3. Guarda [`feature_todo`](app/src/main/java/com/example/taskflow2/feature_todo/)
4. Guarda [Testing Guide](docs/testing.md)
5. Approfondisci [Coroutine e Flow](docs/coroutines-and-flow.md) e [Hilt e DI](docs/hilt.md)

## Licenza

Questa repo usa una licenza personalizzata di tipo "learning-only".

In breve:

- puoi usarla, studiarla e modificarla per imparare
- non puoi usarla per insegnare, fare corsi, workshop, tutoring o contenuti formativi derivati

Testo completo in [LICENSE](LICENSE).
