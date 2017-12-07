package nl.pocketquest.server.request.handler

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.server.firebase.FBChildListener
import nl.pocketquest.server.request.Request
import nl.pocketquest.server.utils.getLogger
import nl.pocketquest.server.utils.remove


class BaseHandler<T : Request>(private val handler: RequestHandler<T>) {

    fun start() {
        FirebaseDatabase.getInstance()
                .getReference(handler.listenPath())
                .addChildEventListener(object : FBChildListener() {
                    override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
                        handleRequest(snapshot)
                    }
                })
    }

    private fun handleRequest(snapshot: DataSnapshot?) {
        try {
            handleRequestUnsafe(snapshot)
        } catch (e: Exception) {
            getLogger().error("Can't parse request", e)
        }
        async(CommonPool) {
            val success = snapshot?.ref?.remove() ?: true
            if (!success) {
                getLogger().warn("Failed to remove request after handling")
            }
        }

    }

    private fun handleRequestUnsafe(snapshot: DataSnapshot?) {
        val request = snapshot?.getValue(handler.requestType()) ?: return
        request.requestID = snapshot.key
        async(CommonPool) {
            try {
                val response = handler.handle(request)
                handleResponse(response)
            } catch (e: Exception) {
                getLogger().error("Exception while handling request", e)
            }
        }
    }

    private suspend fun handleResponse(response: Response) {
        getLogger().info("received response {}", response)
    }
}