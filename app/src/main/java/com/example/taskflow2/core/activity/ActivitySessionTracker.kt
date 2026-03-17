package com.example.taskflow2.core.activity

import android.content.Context
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

/**
 * Esempio didattico di dipendenza `@ActivityScoped`.
 *
 * Questa classe vive finche' vive una singola `Activity`.
 * Ha senso qui perche' dipende da `@ActivityContext`, quindi non deve mai
 * essere promossa a `@Singleton`: farlo significherebbe rischiare di trattenere
 * un riferimento a una Activity distrutta, causando memory leak.
 *
 * Regola pratica anti-leak:
 * - se un oggetto dipende da `Activity`, `Fragment`, view o `ActivityContext`,
 *   non renderlo `@Singleton`
 * - tienilo nella scope piu' stretta compatibile con il suo vero ciclo di vita
 */
@ActivityScoped
class ActivitySessionTracker @Inject constructor(
    @ActivityContext private val activityContext: Context
) {
    private val sessionTag = buildString {
        append(activityContext.javaClass.simpleName)
        append("@")
        append(Integer.toHexString(System.identityHashCode(activityContext)))
    }

    fun currentSessionTag(): String = sessionTag
}
