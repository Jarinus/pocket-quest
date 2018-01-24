package nl.pocketquest.server.api.resource

import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.DatabaseConfiguration
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.api.state.State

data class ResourceInventoryRoute(private val id: String) : Findable<Map<String, Long>> {
    override val route = listOf("resource_instances", id, "resources_left")
    override val expectedType: Class<Map<String, Long>> = Map::class.java as Class<Map<String, Long>>
}

data class ResourceTypeRoute(private val id: String) : Findable<String> {
    override val route = listOf("resource_instances", id, "type")
    override val expectedType = String::class.java
}

open class ResourceInstance internal constructor(val inventory: Inventory, private val typeSource: DataSource<String>) {

    open suspend fun resourceNode() = typeSource.readAsync()?.let { State.resourceNode(it) }

    companion object {
        fun byId(id: String) = ResourceInstance(
                Inventory(ResourceInventoryRoute(id).route),
                DatabaseConfiguration.database.resolver.resolve(ResourceTypeRoute(id))
        )
    }
}