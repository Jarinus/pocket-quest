package nl.pocketquest.server.state

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.entity.*
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
        val resourceNodes = FirebaseDatabase.getInstance()
                .getReference("/entities/resource_nodes")
                .readAsync<HashMap<String, ResourceNodeModel>>()

        loadResourceSuppliedItems()
                .forEach({ resourceNodeId, suppliedItems ->
                    resourceNodes[resourceNodeId]?.suppliedItems = suppliedItems
                })

        return resourceNodes.mapValues { it.value.toResourceNode(it.key) }
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

    private suspend fun loadResourceSuppliedItems(): Map<String, Map<String, ResourceNodeSuppliedItem>> {
        return FirebaseDatabase.getInstance()
                .getReference("/entities/resource_node_supplied_items")
                .readAsync<HashMap<String, HashMap<String, ResourceNodeSuppliedItemModel>>>()
                .mapValues { it.value.mapValues { it.value.toSuppliedItem(it.key) } }
    }
}
