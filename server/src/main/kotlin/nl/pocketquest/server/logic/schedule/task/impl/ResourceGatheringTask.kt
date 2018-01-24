package nl.pocketquest.server.logic.schedule.task.impl

import nl.pocketquest.server.logic.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.api.resource.ResourceInstance
import nl.pocketquest.server.logic.schedule.task.Task
import nl.pocketquest.server.api.user.Status
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.utils.getLogger
import java.util.concurrent.TimeUnit


class ResourceGatheringTask(
        interval: Number,
        timeUnit: TimeUnit,
        private val request: ResourceGatheringRequest
) : Task(interval, timeUnit, true) {

    private val user = User.byId(request.user_id)
    private val resource = ResourceInstance.byId(request.resource_node_uid)

    suspend override fun beforeDestruction() {
        user.setStatus(Status.IDLE)
    }

    override suspend fun execute() {
        resource.inventory.transferTo(request.resource_id, 1, user.inventory)
        scheduleNext = resource.inventory.item(request.resource_id).count() > 0
    }
}
