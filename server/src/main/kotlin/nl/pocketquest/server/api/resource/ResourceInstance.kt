package nl.pocketquest.server.api.resource

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.dataaccesslayer.Database

const val RESOURCE_INSTANCES = "resource_instances"

data class ResourceInventoryRoute(private val id: String) : Findable<Map<String, Long>> {
    override val route = listOf(RESOURCE_INSTANCES, id, "resources_left")
    override val expectedType: Class<Map<String, Long>> = Map::class.java as Class<Map<String, Long>>
}

data class ResourceTypeRoute(private val id: String) : Findable<String> {
    override val route = listOf(RESOURCE_INSTANCES, id, "type")
    override val expectedType = String::class.java
}

open class ResourceInstance internal constructor(
        val inventory: Inventory,
        private val typeSource: DataSource<String>,
        private val kodein: Kodein) {

    open suspend fun resourceNode() = typeSource.readAsync()?.let { kodein.instance<Entities>().resourceNode(it) }

    companion object {
        fun byId(id: String, kodein: Kodein) = ResourceInstance(
                Inventory(ResourceInventoryRoute(id).route, kodein),
                kodein.instance<Database>()
                        .resolver
                        .resolve(ResourceTypeRoute(id)),
                kodein
        )
    }
}