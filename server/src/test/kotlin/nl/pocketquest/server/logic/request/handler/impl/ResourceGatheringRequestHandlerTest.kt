package nl.pocketquest.server.logic.request.handler.impl

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.entity.CraftingRecipe
import nl.pocketquest.server.api.entity.ResourceNode
import nl.pocketquest.server.api.entity.ResourceNodeSuppliedItem
import nl.pocketquest.server.api.resource.ResourceTypeRoute
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.api.user.Status
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
    private val treeType = ResourceTypeRoute("chocolate_tree")

    private val entities = object : Entities {
        override fun item(identifier: String) = null

        override fun resourceNode(identifier: String) = ResourceNode(
                "tree_1", "trees", "", "Chocolate Tree", "1", "", mapOf(
                "chocolate_wood" to ResourceNodeSuppliedItem("chocolate_wood", 5, 5 to 6, 20)
        )
        ).takeIf { identifier == "tree_1" }


        override fun resourceNodeFamily(identifier: String) = null
        override fun recipe(identifier: String) = null
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
        db.add(treeType.route, MockDataSource("tree_1"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", 5000, "chocolate_tree")
        runBlocking {
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response(null, 200), response)
            assertTrue(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    5
            ), 5000L)))
        }
    }

    @Test
    fun shouldGatherWhileIdle() {
        db.add(userStatus.route, MockDataSource(Status.IDLE.identifier))
        db.add(treeType.route, MockDataSource("tree_1"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", 5000, "chocolate_tree")
        runBlocking {
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response(null, 200), response)
            assertTrue(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    5
            ), 5000L)))
        }
    }

    @Test
    fun shouldDeclineBecauseGathering() {
        db.add(userStatus.route, MockDataSource(Status.GATHERING.identifier))
        db.add(treeType.route, MockDataSource("tree_1"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", 5000, "chocolate_tree")
        runBlocking {
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(Response(null, 200), response)
            assertFalse(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    5
            ), 5000L)))
        }
    }

    @Test
    fun shouldNotHandleInvalidRequest() {
        db.add(userStatus.route, MockDataSource(Status.GATHERING.identifier))
        db.add(treeType.route, MockDataSource("type_is_wrong"))
        val request = ResourceGatheringRequest("1", "chocolate_bear", "chocolate_wood", 5000, "chocolate_tree")
        runBlocking {
            val response = ResourceGatheringRequestHandler(kodein)
                    .handle(request, MockDataSource(request))
            assertEquals(400, response.statusCode)
            assertFalse(eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, ResourceGatheringData(
                    "chocolate_bear",
                    "chocolate_tree",
                    "chocolate_wood",
                    5
            ), 5000L)))
        }
    }
}