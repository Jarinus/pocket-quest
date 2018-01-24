package nl.pocketquest.server.logic.schedule.task

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.server.logic.schedule.Scheduler
import nl.pocketquest.server.utils.getLogger
import java.util.concurrent.TimeUnit

abstract class Task(
        val interval: Number,
        val timeUnit: TimeUnit,
        scheduleNext: Boolean = false
) {
    var iterationCount = 0
        private set
    var scheduleNext = scheduleNext
        protected set

    suspend abstract fun execute()
    /**
     * Will be called after the last execution of this task
     */
    suspend open fun beforeDestruction() = Unit

    open fun run() {
        iteration()
    }

    private fun iteration() {
        Scheduler.scheduleAfter(interval.toLong(), timeUnit, {
            async(CommonPool) {
                try {
                    execute()
                } catch (e: Exception) {
                    getLogger().error("{}", e)
                }
                iterationCount++
                if (scheduleNext) {
                    iteration()
                } else {
                    beforeDestruction()
                }
            }
        })
    }
}
