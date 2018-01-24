package nl.pocketquest.server.logic.request.handler.impl

import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.logic.request.handler.RequestHandler
import nl.pocketquest.server.logic.request.handler.Response
import nl.pocketquest.server.logic.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.api.resource.ResourceInstance
import nl.pocketquest.server.api.resource.ResourceInstance.Companion.byId
import nl.pocketquest.server.logic.schedule.task.impl.ResourceGatheringTask
import nl.pocketquest.server.api.user.Status
import nl.pocketquest.server.api.user.updateUser
import nl.pocketquest.server.logic.schedule.task.Task
import java.util.concurrent.TimeUnit

class ResourceRequestRoute : Findable<ResourceGatheringRequest> {
    override val route = listOf("requests", "resource_gathering")
    override val expectedType = ResourceGatheringRequest::class.java
}

class ResourceGatheringRequestHandler internal constructor(
        private val resourceResolver: (String) -> ResourceInstance = ::byId,
        private val taskTester: (Task) -> Task = { it }
) : RequestHandler<ResourceGatheringRequest> {


    suspend override fun handle(request: ResourceGatheringRequest, requestReference: DataSource<ResourceGatheringRequest>): Response {
        val interval = resourceResolver(request.resource_node_uid).resourceNode()
                ?.suppliedItems
                ?.get(request.resource_id)
                ?.duration
                ?: return Response("Invalid request", 400)
        updateUser(request.user_id) {
            if (setStatus(Status.GATHERING)) {
                val task = taskTester(ResourceGatheringTask(interval, TimeUnit.SECONDS, request))
                task.run()
            }
        }
        return Response(null, 200)
    }

    override val route = ResourceRequestRoute()
}
