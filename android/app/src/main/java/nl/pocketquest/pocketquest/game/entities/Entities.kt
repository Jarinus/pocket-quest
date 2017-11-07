package nl.pocketquest.pocketquest.game.entities

import com.google.firebase.database.ServerValue
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.readAsync
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.wtf

data class FBResourceGatherRequest(
        val resource_node_uid: String,
        val requested_at: Map<String, String> = ServerValue.TIMESTAMP
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

/**
 * Created by Laurens on 6-11-2017.
 */
object Entities : AnkoLogger {
    var items = mapOf<String, FBItem>()
        private set
    var resource_nodes = mapOf<String, FBResourceNode>()
        private set

    init {
        async(CommonPool) {
            try {
                items = DATABASE.getReference("entities/items").readAsync<HashMap<String, FBItem>>()
                resource_nodes = DATABASE.getReference("entities/resource_nodes").readAsync<HashMap<String, FBResourceNode>>()
            } catch (e: Exception) {
                wtf(e)
                error(e.getStackTraceString())
            }
        }
    }
}
