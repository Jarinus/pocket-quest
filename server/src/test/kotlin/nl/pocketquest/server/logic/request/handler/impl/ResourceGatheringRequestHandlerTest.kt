package nl.pocketquest.server.logic.request.handler.impl

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.entity.CraftingRecipe
import nl.pocketquest.server.api.entity.ResourceNode
import nl.pocketquest.server.api.entity.ResourceNodeSuppliedItem
import nl.pocketquest.server.api.entity.*
import nl.pocketquest.server.api.resource.ResourceTypeRoute
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.api.user.InventoryRoute
import nl.pocketquest.server.api.user.Status
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.api.user.UserStatusRoute
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventPool
import nl.pocketquest.server.logic.events.impl.DefaultEventPool
import nl.pocketquest.server.logic.request.handler.Response
import nl.pocketquest.server.logic.request.impl.ResourceGatheringRequest
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringData
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringStatus
import nl.pocketquest.server.testhelpers.MockDataSource
import nl.pocketquest.server.testhelpers.NoOpDispatcher
import nl.pocketquest.server.testhelpers.TestDB
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ResourceGatheringRequestHandlerTest {

    val eventPool = DefaultEventPool(NoOpDispatcher())
    val db = TestDB(mapOf())
    private val userStatus = UserStatusRoute("chocolate_bear")
    private val treeTypeLowTier = ResourceTypeRoute("chocolate_tree")
    private val treeTypeHighTier = ResourceTypeRoute("hazelnut_chocolate_tree")

    private val entities = object : Entities {
        override fun recipe(identifier: String): CraftingRecipe? = null
        override fun item(identifier: String) = when (identifier) {
            "hatchet_3" -> Item("hatchet_3", "", "hazelnut_chocolate_hatchet", "3")
            "pickaxe_3" -> Item("pickaxe_3", "", "hazelnut_chocolate_pickaxe", "3")
            "hatchet_1" -> Item("hatchet_1", "", "chocolate_hatchet", "1")
            else -> null
        }

        private val treeLowTier = ResourceNode(
                "tree_1", "trees", "", "Chocolate Tree", "1", "", mapOf(
                "chocolate_wood" to ResourceNodeSuppliedItem("chocolate_wood", 5, 5 to 6, 20))
        )

        private val treeHighTier = ResourceNode(
                "tree_3", "trees", "", "Hazelnut Chocolate Tree", "3", "", mapOf(
                "hazelnut_chocolate_wood" to ResourceNodeSuppliedItem("hazelnut_chocolate_wood", 5, 2 to 3, 50))
        )

        private val oreRock = ResourceNode(
                "ore_rock_1", "ore_rocks", "", "Chocolate Ore", "3", "", mapOf(
                "chocolate_chunk" to ResourceNodeSuppliedItem("chocolate_chunk", 15, 5 to 6, 30))
        )

        override fun resourceNode(identifier: String) = when (identifier) {
            "tree_1" -> treeLowTier
            "tree_3" -> treeHighTier
            "orerock_1" -> oreRock
            else -> null
        }

        override fun gatheringToolFamily(identifier: String) = when (identifier) {
            "pickaxe" -> GatheringToolFamily("pickaxe", listOf("pickaxe_3"))
            "hatchet" -> GatheringToolFamily("hatchet", listOf("hatchet_1", "hatchet_3"))
            else -> null
        }

        override fun resourceNodeFamily(identifier: String) = when (identifier) {
            "trees" -> ResourceNodeFamily("trees", listOf("hatchet"), listOf(treeLowTier, treeHighTier))
            "ore_rocks" -> ResourceNodeFamily("ore_rocks", listOf("pickaxe"), listOf(oreRock))
            else -> null
        }
    }

    private val kodein = Kodein {
        bind<EventPool>() with singleton { eventPool }
        bind<Database>() with singleton { db }
        bind<Entities>() with singleton { entities }
    }

    @Before
    fun setup() {
        eventPool.clear()
        db.clear()
    }

    @Test
    fun shouldGatherWithoutStatus() {
        db.add(treeTypeLowTier.route, MockDataSource("tree_1"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", "chocolate_hatchet", 5000, "chocolate_tree")
        runBlocking {
            User.byId("chocolate_bear", kodein).inventory.item("chocolate_hatchet").give(1L)
            User.byId("chocolate_bear", kodein).inventory.item("hazelnut_chocolate_pickaxe").give(1L)
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response(null, 200), response)
            assertTrue(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    "chocolate_hatchet",
                    5000
            ), 5000L)))
        }
    }

    @Test
    fun shouldGatherFasterWithoutStatus() {
        db.add(treeTypeLowTier.route, MockDataSource("tree_1"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", "chocolate_hatchet", 5000, "chocolate_tree")
        runBlocking {
            User.byId("chocolate_bear", kodein).inventory.item("chocolate_hatchet").give(1L)
            User.byId("chocolate_bear", kodein).inventory.item("hazelnut_chocolate_pickaxe").give(1L)
            User.byId("chocolate_bear", kodein).inventory.item("hazelnut_chocolate_hatchet").give(1L)
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response(null, 200), response)
            assertTrue(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    "chocolate_hatchet",
                    3572
            ), 5000L)))
        }
    }

    @Test
    fun shouldNotGatherTierToLowWithoutStatus() {
        db.add(treeTypeHighTier.route, MockDataSource("tree_3"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "hazelnut_chocolate_wood", "chocolate_hatchet", 5000, "hazelnut_chocolate_tree")
        runBlocking {
            User.byId("chocolate_bear", kodein).inventory.item("chocolate_hatchet").give(1L)
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response("User does not have the right tool", 403), response)
            assertFalse(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "hazelnut_chocolate_tree",
                    "hazelnut_chocolate_wood",
                    "chocolate_hatchet",
                    5000
            ), 5000L)))
        }
    }


    @Test
    fun shouldGatherWhileIdle() {
        db.add(userStatus.route, MockDataSource(Status.IDLE.identifier))
        db.add(treeTypeLowTier.route, MockDataSource("tree_1"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", "chocolate_hatchet", 5000, "chocolate_tree")
        runBlocking {
            User.byId("chocolate_bear", kodein).inventory.item("chocolate_hatchet").give(1L)
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response(null, 200), response)
            assertTrue(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    "chocolate_hatchet",
                    5000
            ), 5000L)))
        }
    }

    @Test
    fun shouldDeclineBecauseGathering() {
        db.add(userStatus.route, MockDataSource(Status.GATHERING.identifier))
        db.add(treeTypeLowTier.route, MockDataSource("tree_1"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", "chocolate_hatchet", 5000, "chocolate_tree")
        runBlocking {
            User.byId("chocolate_bear", kodein).inventory.item("chocolate_hatchet").give(1L)
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response(null, 200), response)
            assertFalse(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    "chocolate_hatchet",
                    5000
            ), 5000L)))
        }
    }

    @Test
    fun shouldNotHandleInvalidRequest() {
        db.add(userStatus.route, MockDataSource(Status.GATHERING.identifier))
        db.add(treeTypeLowTier.route, MockDataSource("type_is_wrong"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", "chocolate_hatchet", 5000, "chocolate_tree")
        runBlocking {
            User.byId("chocolate_bear", kodein).inventory.item("chocolate_hatchet").give(1L)
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(404, response.statusCode)
            assertFalse(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    "chocolate_hatchet",
                    5000
            ), 5000L)))
        }
    }

}