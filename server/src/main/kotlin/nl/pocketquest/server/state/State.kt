package nl.pocketquest.server.state

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.entity.ResourceNode
import nl.pocketquest.server.entity.ResourceNodeFamily
import nl.pocketquest.server.entity.ResourceNodeFamilyModel
import nl.pocketquest.server.entity.ResourceNodeModel
import nl.pocketquest.server.utils.readAsync

object State {
    private lateinit var resourceNodes: Map<String, ResourceNode>
    private lateinit var resourceNodeFamilies: Map<String, ResourceNodeFamily>

    fun init() {
        runBlocking {
            resourceNodes = loadResourceNodes()
            resourceNodeFamilies = loadResourceNodeFamilies()
        }
    }

    fun resourceNode(identifier: String): ResourceNode? {
        return resourceNodes[identifier]
    }

    fun resourceNodeFamily(identifier: String): ResourceNodeFamily? {
        return resourceNodeFamilies[identifier]
    }

    private suspend fun loadResourceNodes(): Map<String, ResourceNode> {
        return FirebaseDatabase.getInstance()
                .getReference("/entities/resource_nodes")
                .readAsync<HashMap<String, ResourceNodeModel>>()
                .mapValues { it.value.toResourceNode(it.key) }
    }

    private suspend fun loadResourceNodeFamilies(): Map<String, ResourceNodeFamily> {
        val resourceNodeFamilyModels = FirebaseDatabase.getInstance()
                .getReference("/entities/resource_node_families")
                .readAsync<HashMap<String, ResourceNodeFamilyModel>>()

        loadResourceNodeFamilyMembers()
                .mapValues { it.value.mapNotNull(this::resourceNode) }
                .forEach({ key, value ->
                    resourceNodeFamilyModels[key]?.members = value
                })

        return resourceNodeFamilyModels
                .mapValues { it.value.toResourceNodeFamily(it.key) }
    }

    private suspend fun loadResourceNodeFamilyMembers(): Map<String, Set<String>> {
        return FirebaseDatabase.getInstance()
                .getReference("/entities/resource_node_resource_node_families")
                .readAsync<HashMap<String, HashMap<String, Boolean>>>()
                .mapValues { it.value.keys }
    }
}
