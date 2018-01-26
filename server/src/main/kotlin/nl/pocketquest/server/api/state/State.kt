package nl.pocketquest.server.api.state

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.dataaccesslayer.DatabaseConfiguration
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

class EntitesRoute : Findable<EntitiesModel> {
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
                val entities = database.resolver.resolve(EntitesRoute()).readAsync()
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

    private suspend fun loadItems(entitesModel: EntitiesModel): Map<String, Item> = entitesModel.items
            .mapValues { it.value.toItem(it.key) }


    private suspend fun loadResourceNodes(entitesModel: EntitiesModel): Map<String, ResourceNode> {
        val resourceNodes = entitesModel.resource_nodes

        loadResourceSuppliedItems(entitesModel).forEach { resourceNodeId, suppliedItems ->
            resourceNodes[resourceNodeId]?.suppliedItems = suppliedItems
        }

        return resourceNodes.mapValues { it.value.toResourceNode(it.key) }
    }

    private suspend fun loadResourceNodeFamilies(entitesModel: EntitiesModel): Map<String, ResourceNodeFamily> {
        val resourceNodeFamilyModels = entitesModel.resource_node_families

        loadResourceNodeFamilyMembers(entitesModel)
                .mapValues { it.value.mapNotNull(this::resourceNode) }
                .forEach { key, value ->
                    resourceNodeFamilyModels[key]?.members = value
                }

        return resourceNodeFamilyModels.mapValues { it.value.toResourceNodeFamily(it.key) }
    }

    private suspend fun loadResourceNodeFamilyMembers(entitesModel: EntitiesModel): Map<String, Set<String>> = entitesModel.resource_node_resource_node_families
            .mapValues { it.value.keys }


    private suspend fun loadResourceSuppliedItems(entitesModel: EntitiesModel): Map<String, Map<String, ResourceNodeSuppliedItem>> = entitesModel.resource_node_supplied_items
            .mapValues { it.value.mapValues { it.value.toSuppliedItem(it.key) } }
}
