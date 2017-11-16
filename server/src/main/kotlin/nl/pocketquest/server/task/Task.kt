package nl.pocketquest.server.task

import nl.pocketquest.server.schedule.Scheduler
import java.util.concurrent.TimeUnit

abstract class Task(
        private val interval: Long,
        private val timeUnit: TimeUnit,
        protected var scheduleNext: Boolean = false
) {
    var iterationCount = 0
        private set

    protected abstract fun validate(): Boolean
    protected abstract fun execute()

    fun run() {
        iteration()
    }

    private fun iteration() {
        Scheduler.scheduleAfter(interval, timeUnit, {
            if (validate()) {
                execute()
                iterationCount++

                if (scheduleNext) {
                    iteration()
                }
            }
        })
    }
}
