package nl.pocketquest.pocketquest.utils

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlin.coroutines.experimental.CoroutineContext

fun <T> loopAsynchronous(
        context: CoroutineContext,
        sleepDuration: Number = 0,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
) = async(context, start) {
    while (isActive) {
        block()
        delay(sleepDuration.toLong())
    }
}
