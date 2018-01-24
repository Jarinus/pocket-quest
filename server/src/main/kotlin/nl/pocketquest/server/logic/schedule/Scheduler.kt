package nl.pocketquest.server.logic.schedule

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

object Scheduler {
    private val timer = Timer()

    fun scheduleAfter(duration: Long, timeUnit: TimeUnit, task: () -> Unit) {
        val timerTask = timerTask { task() }
        val durationMillis = timeUnit.toMillis(duration)

        timer.schedule(timerTask, durationMillis)
    }
}
