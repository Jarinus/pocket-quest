package nl.pocketquest.server.api

import kotlinx.coroutines.experimental.NonCancellable
import kotlinx.coroutines.experimental.NonCancellable.isActive
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import nl.pocketquest.server.logic.schedule.task.Task
import nl.pocketquest.server.utils.getLogger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class TestableTask(var task: Task) : Task(task.interval, task.timeUnit, task.scheduleNext) {

    var isActive = true

    suspend override fun execute() {
        task.execute()
        scheduleNext = task.scheduleNext
    }

    suspend override fun beforeDestruction() {
        task.beforeDestruction()
        isActive = false
    }

    suspend fun waitToFinish() {
        while (isActive) {
            delay(100)
        }
    }
}