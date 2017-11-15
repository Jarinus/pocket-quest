package nl.pocketquest.server.task

import nl.pocketquest.server.schedule.Scheduler
import java.util.concurrent.TimeUnit

abstract class Task(
        private var iterationsLeft: Int,
        private val interval: Long,
        private val timeUnit: TimeUnit
) {
    protected abstract fun validate(): Boolean
    protected abstract fun execute()

    fun run() {
        if (iterationsLeft < 1) {
            return
        }

        iteration()
    }

    private fun iteration() {
        Scheduler.scheduleAfter(interval, timeUnit, {
            if (validate()) {
                execute()
                iterationsLeft--

                if (iterationsLeft > 0) {
                    iteration()
                }
            }
        })
    }
}
