package nl.pocketquest.server.task

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.server.schedule.Scheduler
import java.util.concurrent.TimeUnit

abstract class Task(
        private val interval: Long,
        private val timeUnit: TimeUnit,
        protected var scheduleNext: Boolean = false
) {
    var iterationCount = 0
        private set

    protected suspend abstract fun execute()
    /**
     * Will be called after the last execution of this task
     */
    protected suspend open fun beforeDestruction() = Unit

    fun run() {
        iteration()
    }

    private fun iteration() {
        Scheduler.scheduleAfter(interval, timeUnit, {
            async(CommonPool) {
                execute()
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
