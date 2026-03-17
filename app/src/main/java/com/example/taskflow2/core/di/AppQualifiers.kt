package com.example.taskflow2.core.di

import javax.inject.Qualifier

/**
 * Qualifier custom per distinguere una `String` che rappresenta il package name
 * dell'app da altre `String` che potrebbero esistere nel grafo Hilt.
 *
 * Regola pratica:
 * - quando due o piu' dipendenze hanno lo stesso tipo, Hilt non basta piu'
 *   a capire quale istanza usare
 * - in quei casi un qualifier evita ambiguita' e wiring sbagliati
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AppPackageName
