package nl.pocketquest.server.request.handler.impl

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import nl.pocketquest.server.firebase.FBChildListener
import nl.pocketquest.server.request.handler.RequestHandler
import nl.pocketquest.server.request.handler.Response
import nl.pocketquest.server.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.state.State
import nl.pocketquest.server.task.impl.ResourceGatheringTask
import nl.pocketquest.server.user.Status
import nl.pocketquest.server.user.User
import nl.pocketquest.server.user.updateUser
import nl.pocketquest.server.utils.DATABASE
import nl.pocketquest.server.utils.getLogger
import nl.pocketquest.server.utils.readAsync
import java.util.concurrent.TimeUnit

object ResourceGatheringRequestHandler : RequestHandler<ResourceGatheringRequest> {

    override fun listenPath() = "requests/resource_gathering"

    suspend override fun handle(request: ResourceGatheringRequest): Response {
        val resourceNodeID = DATABASE.getReference("resource_instances/${request.resource_node_uid}/type").readAsync<String>()
        val interval = State.resourceNode(resourceNodeID)
                ?.suppliedItems
                ?.get(request.resource_id)
                ?.duration
                ?: return Response("Invalid request", 400)
        updateUser(request.user_id) {
            if (setStatus(Status.GATHERING)) {
                ResourceGatheringTask(interval, TimeUnit.SECONDS, request).run()
            }
        }
        return Response(null, 200)
    }

    override fun requestType() = ResourceGatheringRequest::class.java
}
