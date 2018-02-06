package nl.pocketquest.server.logic.schedule.resourcegathering.impl

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.item.Inventory
import nl.pocketquest.server.api.item.ItemRoute
import nl.pocketquest.server.api.resource.ResourceInventoryRoute
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.api.user.UserStatusRoute
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventPool
import nl.pocketquest.server.logic.events.impl.DefaultEventPool
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringData
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringGainHandler
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringStatus
import nl.pocketquest.server.testhelpers.MockDataSource
import nl.pocketquest.server.testhelpers.NoOpDispatcher
import nl.pocketquest.server.testhelpers.TestDB
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ResourceGatheringGainHandlerTest {

    val eventPool = DefaultEventPool(NoOpDispatcher())
    val db = TestDB(mapOf())
    private val kodein = Kodein {
        bind<EventPool>() with singleton { eventPool }
        bind<Database>() with singleton { db }
    }

    private val treeResourceRoute = ItemRoute(Inventory(ResourceInventoryRoute("chocolate_tree").route, kodein), "chocolate_wood")
    private val userStatus = UserStatusRoute("chocolate_bear")
    private val user = User.byId("chocolate_bear", kodein)
    val eventData = ResourceGatheringData(
            "chocolate_bear",
            "chocolate_tree",
            "chocolate_wood",
            "chocolate_hatchet",
            5
    )

    @Before
    fun setup() {
        eventPool.clear()
        db.clear()
    }

    @Test
    fun shouldGatherResources() {
        val treeWood = MockDataSource(6L)
        db.add(treeResourceRoute.route, treeWood)
        runBlocking {
            ResourceGatheringGainHandler(kodein)
                    .handle(Event.of(ResourceGatheringStatus.GAINS_RESOURCE, eventData, 1000L))
            assertEquals(1L, user.inventory.item("chocolate_wood").count())
            assertEquals(5L, treeWood.content)
            assertTrue(
                    eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, eventData, 1000L))
            )
        }
    }

    @Test
    fun shouldNotGatherEmptyTree() {
        val treeWood = MockDataSource(0L)
        db.add(treeResourceRoute.route, treeWood)
        runBlocking {
            ResourceGatheringGainHandler(kodein)
                    .handle(Event.of(ResourceGatheringStatus.GAINS_RESOURCE, eventData, 1000L))
            assertEquals(0L, user.inventory.item("chocolate_wood").count())
            assertEquals(0L, treeWood.content)
            assertTrue(
                    eventPool.contains(Event.of(ResourceGatheringStatus.STARTED_GATHERING, eventData, 1000L))
            )
        }
    }


}