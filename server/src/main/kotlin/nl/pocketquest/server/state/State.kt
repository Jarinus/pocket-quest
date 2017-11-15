package nl.pocketquest.server.state

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.entity.ResourceNode
import nl.pocketquest.server.utils.readAsync

object State {
    private lateinit var resourceNodes: Map<String, ResourceNode>

    fun init() {
        runBlocking {
            resourceNodes = loadResourceNodes()
        }
    }

    fun resourceNode(identifier: String): ResourceNode? {
        return resourceNodes[identifier]
    }

    private suspend fun loadResourceNodes(): Map<String, ResourceNode> {
        return FirebaseDatabase.getInstance()
                .getReference("/entities/resource_nodes")
                .readAsync<HashMap<String, ResourceNode>>()
    }
}
