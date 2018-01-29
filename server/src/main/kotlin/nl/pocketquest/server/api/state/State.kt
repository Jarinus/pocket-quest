package nl.pocketquest.server.api.state

import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.api.entity.*
import nl.pocketquest.server.dataaccesslayer.Database

class EntitiesModel {
    lateinit var items: HashMap<String, ItemModel>
    lateinit var resource_nodes: HashMap<String, ResourceNodeModel>
    lateinit var resource_node_families: HashMap<String, ResourceNodeFamilyModel>
    lateinit var resource_node_resource_node_families: HashMap<String, HashMap<String, Boolean>>
    lateinit var resource_node_supplied_items: HashMap<String, HashMap<String, ResourceNodeSuppliedItemModel>>
}

interface Entities {
    fun item(identifier: String): Item?
    fun resourceNode(identifier: String): ResourceNode?
    fun resourceNodeFamily(identifier: String): ResourceNodeFamily?
}

class EntitiesRoute : Findable<EntitiesModel> {
    override val route = listOf("entities")
    override val expectedType = EntitiesModel::class.java
}

class State(private val database: Database) : Entities {
    private lateinit var items: Map<String, Item>
    private lateinit var resourceNodes: Map<String, ResourceNode>
    private lateinit var resourceNodeFamilies: Map<String, ResourceNodeFamily>

    init {
        try {
            runBlocking {
                val entities = database.resolver
                        .resolve(EntitiesRoute())
                        .readAsync()
                        ?: throw IllegalStateException("Failed to load entities")
                items = loadItems(entities)
                resourceNodes = loadResourceNodes(entities)
                resourceNodeFamilies = loadResourceNodeFamilies(entities)
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override fun item(identifier: String) = items[identifier]

    override fun resourceNode(identifier: String) = resourceNodes[identifier]

    override fun resourceNodeFamily(identifier: String) = resourceNodeFamilies[identifier]

    private suspend fun loadItems(entitiesModel: EntitiesModel): Map<String, Item> = entitiesModel.items
            .mapValues { it.value.toItem(it.key) }


    private suspend fun loadResourceNodes(entitiesModel: EntitiesModel): Map<String, ResourceNode> {
        val resourceNodes = entitiesModel.resource_nodes

        loadResourceSuppliedItems(entitiesModel).forEach { resourceNodeId, suppliedItems ->
            resourceNodes[resourceNodeId]?.suppliedItems = suppliedItems
        }

        return resourceNodes.mapValues { it.value.toResourceNode(it.key) }
    }

    private suspend fun loadResourceNodeFamilies(entitiesModel: EntitiesModel): Map<String, ResourceNodeFamily> {
        val resourceNodeFamilyModels = entitiesModel.resource_node_families

        loadResourceNodeFamilyMembers(entitiesModel)
                .mapValues { it.value.mapNotNull(this::resourceNode) }
                .forEach { key, value ->
                    resourceNodeFamilyModels[key]?.members = value
                }

        return resourceNodeFamilyModels.mapValues { it.value.toResourceNodeFamily(it.key) }
    }

    private suspend fun loadResourceNodeFamilyMembers(entitiesModel: EntitiesModel): Map<String, Set<String>> = entitiesModel.resource_node_resource_node_families
            .mapValues { it.value.keys }


    private suspend fun loadResourceSuppliedItems(entitiesModel: EntitiesModel): Map<String, Map<String, ResourceNodeSuppliedItem>> = entitiesModel.resource_node_supplied_items
            .mapValues { it.value.mapValues { it.value.toSuppliedItem(it.key) } }
}
