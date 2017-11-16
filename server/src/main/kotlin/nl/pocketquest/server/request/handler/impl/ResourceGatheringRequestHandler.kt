package nl.pocketquest.server.request.handler.impl

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import nl.pocketquest.server.firebase.FBChildListener
import nl.pocketquest.server.request.handler.RequestHandler
import nl.pocketquest.server.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.task.impl.ResourceGatheringTask
import java.util.concurrent.TimeUnit

object ResourceGatheringRequestHandler : RequestHandler<ResourceGatheringRequest>() {
    override fun listen() {
        FirebaseDatabase.getInstance()
                .getReference("/requests/resource_gathering")
                .addChildEventListener(object : FBChildListener() {
                    override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
                        snapshot?.toResourceGatheringRequest()
                                ?.let(this@ResourceGatheringRequestHandler::handle)
                    }
                })
    }

    override fun validateRequest(request: ResourceGatheringRequest): Boolean {
        //TODO: Check (pseudo code) !"gathering".equals(user_state)

        return true
    }

    override fun processRequest(request: ResourceGatheringRequest): Boolean {
        val task = ResourceGatheringTask(1, TimeUnit.SECONDS, request)

        task.run()

        return true
    }
}

private fun DataSnapshot.toResourceGatheringRequest(): ResourceGatheringRequest {
    return this.getValue(ResourceGatheringRequest::class.java)
            .also { it.id = this.key }
}
