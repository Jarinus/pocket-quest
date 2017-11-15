package nl.pocketquest.server.task.impl

import nl.pocketquest.server.state.State
import nl.pocketquest.server.task.Task
import java.util.concurrent.TimeUnit

class ResourceGatheringTask(
        iterationsLeft: Int,
        interval: Long,
        timeUnit: TimeUnit
) : Task(iterationsLeft, interval, timeUnit) {

    override fun validate(): Boolean {
        return true
    }

    override fun execute() {
        println("Gathering resources from: ${State.resourceNode("tree_1")}")
    }

}
