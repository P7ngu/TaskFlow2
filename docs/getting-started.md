# Getting Started

## Requisiti

- Android Studio installato
- JDK/JBR fornito da Android Studio
- SDK Android coerente con il progetto

Valori tecnici correnti:

- `minSdk = 24`
- `targetSdk = 36`
- `compileSdk = 36`
- Java/Kotlin target `11`

## Aprire il progetto

### Android Studio

1. Apri la cartella root `TaskFlow2`
2. Attendi il sync Gradle
3. Se richiesto, installa SDK o Build Tools mancanti
4. Avvia un emulatore o collega un device
5. Esegui il modulo `app`

### Terminale

Build debug:

```bash
./gradlew assembleDebug
```

Test unitari:

```bash
./gradlew test
```

APK generato:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Cosa vedrai all'avvio

- schermata TODO principale
- header Home integrato nella parte alta
- feature TODO con lista e inserimento task
- feature Home con dati locali/remoti mockati in modo didattico

## Da Dove Iniziare a Leggere

Ordine consigliato:

1. [`MainActivity.kt`](../app/src/main/java/com/example/taskflow2/MainActivity.kt)
2. [`feature_todo/`](../app/src/main/java/com/example/taskflow2/feature_todo/)
3. [`feature_home/`](../app/src/main/java/com/example/taskflow2/feature_home/)
4. [`samples/`](../app/src/main/java/com/example/taskflow2/samples/)
5. [`app/src/test/java/com/example/taskflow2/`](../app/src/test/java/com/example/taskflow2/)

## Note Utili

- `local.properties` e' locale e non va condiviso
- `build/`, `.gradle/` e file generati non fanno parte del codice sorgente da studiare
- i test sono parte integrante della documentazione: spesso spiegano i concetti meglio di una sola pagina teorica
