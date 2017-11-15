package nl.pocketquest.server.request.handler.impl

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import nl.pocketquest.server.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.request.handler.RequestHandler
import nl.pocketquest.server.task.impl.ResourceGatheringTask
import java.util.concurrent.TimeUnit

object ResourceGatheringRequestHandler : RequestHandler<ResourceGatheringRequest>() {
    override fun listen() {
        val childEventListener = getChildEventListener()

        FirebaseDatabase.getInstance()
                .getReference("/requests/resource_gathering")
                .addChildEventListener(childEventListener)
    }

    override fun validateRequest(request: ResourceGatheringRequest): Boolean {
        return true
    }

    override fun processRequest(request: ResourceGatheringRequest): Boolean {
        val task = ResourceGatheringTask(8, 1, TimeUnit.SECONDS)

        task.run()

        return true
    }

    //TODO: Wrap in Abstract Class (to enable specific method implementations)
    private fun getChildEventListener(): ChildEventListener {
        return object : ChildEventListener {
            override fun onCancelled(error: DatabaseError?) {
            }

            override fun onChildMoved(snapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildChanged(snapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
                snapshot?.toResourceGatheringRequest()
                        ?.let(this@ResourceGatheringRequestHandler::handle)
            }

            override fun onChildRemoved(snapshot: DataSnapshot?) {
            }
        }
    }
}

private fun DataSnapshot.toResourceGatheringRequest(): ResourceGatheringRequest {
    return this.getValue(ResourceGatheringRequest::class.java)
            .also { it.id = this.key }
}
