package nl.pocketquest.server.logic.request.handler.impl

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.logic.request.handler.RequestHandler
import nl.pocketquest.server.logic.request.handler.Response
import nl.pocketquest.server.logic.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.api.resource.ResourceInstance
import nl.pocketquest.server.api.user.Status
import nl.pocketquest.server.api.user.updateUser
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringData
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringStatus
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventPool

class ResourceRequestRoute : Findable<ResourceGatheringRequest> {
    override val route = listOf("requests", "resource_gathering")
    override val expectedType = ResourceGatheringRequest::class.java
}

class ResourceGatheringRequestHandler internal constructor(
        private val kodein: Kodein
) : RequestHandler<ResourceGatheringRequest> {

    private val eventPool: EventPool = kodein.instance()

    suspend override fun handle(request: ResourceGatheringRequest, requestReference: DataSource<ResourceGatheringRequest>): Response {
        val interval = ResourceInstance.byId(request.resource_node_uid, kodein).resourceNode()
                ?.suppliedItems
                ?.get(request.resource_id)
                ?.duration
                ?: return Response("Invalid request", 400)
        updateUser(request.user_id, kodein) {
            if (setStatus(Status.GATHERING)) {
                eventPool.submit(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                        request.user_id,
                        request.resource_node_uid,
                        request.resource_id,
                        interval
                ), request.requested_at))
            }
        }
        return Response(null, 200)
    }

    override val route = ResourceRequestRoute()
}
