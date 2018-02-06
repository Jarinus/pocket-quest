package nl.pocketquest.pocketquest.utils

import kotlin.coroutines.experimental.buildSequence

operator fun Number.times(other: Number): Number = toDouble() * other.toDouble()
operator fun Number.div(other: Number): Number = toDouble() / other.toDouble()

fun <K, V, R> Map<K, V>.mapKeysNotNull(transform: (Map.Entry<K, V>) -> R?): Map<R, V> =
        asSequence()
                .mapNotNull { entry ->
                    transform(entry)?.let { it to entry.value }
                }
                .toMap()

fun <T> Sequence<T>.repeat(): Sequence<T> = buildSequence {
    val seq = this@repeat.toList()
    while (true) yieldAll(seq)
}
