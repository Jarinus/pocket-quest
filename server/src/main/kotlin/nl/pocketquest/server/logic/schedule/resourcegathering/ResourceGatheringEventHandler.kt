package nl.pocketquest.server.logic.schedule.resourcegathering

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.api.resource.ResourceInstance
import nl.pocketquest.server.api.user.Status
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.api.user.updateUser
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventHandler
import nl.pocketquest.server.logic.events.EventPool
import java.util.concurrent.TimeUnit

object ResourceGatheringHandlers {
    fun handlers(kodein: Kodein) = listOf(
            ResourceGatheringStartHandler(kodein),
            ResourceGatheringGainHandler(kodein)
    )
}

abstract class ResourceGatheringEventHandler(val kodein: Kodein) : EventHandler<ResourceGatheringStatus, ResourceGatheringData> {
    override val typeClass = ResourceGatheringStatus::class.java
    override val dataClass = ResourceGatheringData::class.java
}

/**
 * If resource has resources -> schedules GAINS_RESOURCES event
 * If resource is empty -> sets the status of the player to idle
 */
class ResourceGatheringStartHandler(kodein: Kodein) : ResourceGatheringEventHandler(kodein) {

    override fun isRelevant(type: ResourceGatheringStatus) = type == ResourceGatheringStatus.STARTED_GATHERING

    override suspend fun handle(event: Event<ResourceGatheringStatus, ResourceGatheringData>) {
        val resource = ResourceInstance.byId(event.data.resourceInstanceId, kodein)
        if (resource.inventory.item(event.data.resourceID).count() > 0) {
            val millisecondsInterval = TimeUnit.SECONDS.toMillis(event.data.interval.toLong())
            kodein.instance<EventPool>().submit(
                    Event.of(
                            ResourceGatheringStatus.GAINS_RESOURCE,
                            event.data,
                            event.scheduledFor + millisecondsInterval))
        } else {
            updateUser(event.data.userID, kodein) {
                setStatus(Status.IDLE)
            }
        }
    }
}

/**
 * Tries to transfer item from resource inventory to user inventory.
 */
class ResourceGatheringGainHandler(kodein: Kodein) : ResourceGatheringEventHandler(kodein) {
    override fun isRelevant(type: ResourceGatheringStatus) = type == ResourceGatheringStatus.GAINS_RESOURCE

    suspend override fun handle(event: Event<ResourceGatheringStatus, ResourceGatheringData>) {
        val data = event.data
        val resource = ResourceInstance.byId(data.resourceInstanceId, kodein)
        val user = User.byId(data.userID, kodein)
        resource.inventory.transferTo(data.resourceID, 1, user.inventory)
        kodein.instance<EventPool>().submit(
                Event.of(ResourceGatheringStatus.STARTED_GATHERING, data, event.scheduledFor)
        )
    }

}