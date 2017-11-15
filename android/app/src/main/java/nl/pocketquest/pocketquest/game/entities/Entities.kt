package nl.pocketquest.pocketquest.game.entities

import com.google.firebase.database.ServerValue
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.readAsync
import org.jetbrains.anko.AnkoLogger

data class FBResourceGatherRequest(
        val resource_node_uid: String,
        val requested_at: Map<String, String> = ServerValue.TIMESTAMP,
        val user_id: String
)

data class FBResourceInstance(
        val type: String = "",
        val resources_left: Map<String, Int> = mapOf()
)

data class FBItem(
        val name: String = "",
        val icon: String = "",
        val tier: String = ""
)

data class FBResourceNode(
        val id: String = "",
        val icon: String = "",
        val family: String = "",
        val tier: Int = 0,
        val name: String = "")

object Entities : AnkoLogger {
    private var items: Map<String, FBItem>? = null
    private var resource_nodes: Map<String, FBResourceNode>? = null

    suspend fun getItem(name: String) = getItems()[name]

    suspend fun getItems() = items ?:
            DATABASE.getReference("entities/items")
                    .readAsync<HashMap<String, FBItem>>()
                    .also { items = it }

    suspend fun getResourceNodes() = resource_nodes ?:
            DATABASE.getReference("entities/resource_nodes")
                    .readAsync<HashMap<String, FBResourceNode>>()
                    .also { resource_nodes = it }
}
