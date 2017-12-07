package nl.pocketquest.server.task.impl

import com.google.firebase.database.*
import nl.pocketquest.server.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.task.Task
import nl.pocketquest.server.user.Status
import nl.pocketquest.server.user.User
import nl.pocketquest.server.user.updateUser
import nl.pocketquest.server.utils.DATABASE
import nl.pocketquest.server.utils.incrementBy
import nl.pocketquest.server.utils.incrementByOrCreate
import nl.pocketquest.server.utils.readAsync
import java.util.concurrent.TimeUnit


class ResourceGatheringTask(
        interval: Number,
        timeUnit: TimeUnit,
        private val request: ResourceGatheringRequest
) : Task(interval, timeUnit, true) {

    private val userResourcesRef = DATABASE.getReference("/user_items/${request.user_id}/backpack/${request.resource_id}")
    private val nodeResourcesRef = DATABASE.getReference("/resource_instances/${request.resource_node_uid}/resources_left/${request.resource_id}")

    suspend override fun beforeDestruction() = updateUser(request.user_id) {
        setStatus(Status.IDLE)
    }


    override suspend fun execute() {
        if (nodeResourcesRef.incrementBy(-1, 0, Long.MAX_VALUE)) {
            userResourcesRef.incrementByOrCreate(1, 1)
        }
        scheduleNext = nodeResourcesRef.readAsync<Long>() > 0
    }
}
