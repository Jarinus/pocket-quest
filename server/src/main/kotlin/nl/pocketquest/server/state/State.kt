package nl.pocketquest.server.state

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.entity.*
import nl.pocketquest.server.utils.DATABASE
import nl.pocketquest.server.utils.readAsync

object State {
    private lateinit var items: Map<String, Item>
    private lateinit var resourceNodes: Map<String, ResourceNode>
    private lateinit var resourceNodeFamilies: Map<String, ResourceNodeFamily>

    fun init() {
        runBlocking {
            items = loadItems()
            resourceNodes = loadResourceNodes()
            resourceNodeFamilies = loadResourceNodeFamilies()
        }
    }

    fun item(identifier: String) = items[identifier]

    fun resourceNode(identifier: String) = resourceNodes[identifier]

    fun resourceNodeFamily(identifier: String) = resourceNodeFamilies[identifier]

    private suspend fun loadItems(): Map<String, Item> = DATABASE
            .getReference("/entities/items")
            .readAsync<HashMap<String, ItemModel>>()
            .mapValues { it.value.toItem(it.key) }


    private suspend fun loadResourceNodes(): Map<String, ResourceNode> {
        val resourceNodes = DATABASE
                .getReference("/entities/resource_nodes")
                .readAsync<HashMap<String, ResourceNodeModel>>()

        loadResourceSuppliedItems().forEach { resourceNodeId, suppliedItems ->
            resourceNodes[resourceNodeId]?.suppliedItems = suppliedItems
        }

        return resourceNodes.mapValues { it.value.toResourceNode(it.key) }
    }

    private suspend fun loadResourceNodeFamilies(): Map<String, ResourceNodeFamily> {
        val resourceNodeFamilyModels = DATABASE
                .getReference("/entities/resource_node_families")
                .readAsync<HashMap<String, ResourceNodeFamilyModel>>()

        loadResourceNodeFamilyMembers()
                .mapValues { it.value.mapNotNull(this::resourceNode) }
                .forEach { key, value ->
                    resourceNodeFamilyModels[key]?.members = value
                }

        return resourceNodeFamilyModels.mapValues { it.value.toResourceNodeFamily(it.key) }
    }

    private suspend fun loadResourceNodeFamilyMembers(): Map<String, Set<String>> = DATABASE
            .getReference("/entities/resource_node_resource_node_families")
            .readAsync<HashMap<String, HashMap<String, Boolean>>>()
            .mapValues { it.value.keys }


    private suspend fun loadResourceSuppliedItems(): Map<String, Map<String, ResourceNodeSuppliedItem>> = DATABASE
            .getReference("/entities/resource_node_supplied_items")
            .readAsync<HashMap<String, HashMap<String, ResourceNodeSuppliedItemModel>>>()
            .mapValues { it.value.mapValues { it.value.toSuppliedItem(it.key) } }

}
