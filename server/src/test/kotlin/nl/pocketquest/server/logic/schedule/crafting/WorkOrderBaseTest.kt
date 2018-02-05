package nl.pocketquest.server.logic.schedule.crafting

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.crafting.WorkOrder
import nl.pocketquest.server.api.crafting.WorkOrderModel
import nl.pocketquest.server.api.crafting.WorkOrderQueue
import nl.pocketquest.server.api.crafting.WorkOrderRoute
import nl.pocketquest.server.api.entity.CraftingRecipe
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.api.state.State
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.firebase.Firebase
import nl.pocketquest.server.logic.events.EventDispatcher
import nl.pocketquest.server.logic.events.EventPool
import nl.pocketquest.server.logic.events.impl.DefaultEventDispatcher
import nl.pocketquest.server.logic.events.impl.DefaultEventPool
import nl.pocketquest.server.testhelpers.*
import org.junit.Assert.*

open class WorkOrderBaseTest {

    val db = TestDB(mapOf())
    val dispatcher = TestEventDispatcher()
    val eventPool = DefaultEventPool(dispatcher)
    val entities = object : TestEntities() {
        override fun recipe(identifier: String): CraftingRecipe? = CraftingRecipe(
                "chocolate_bar",
                mapOf(
                        "cacao" to 3L,
                        "milk" to 2L
                ),
                mapOf(
                        "chocolate" to 2L
                ),
                4L
        ).takeIf { identifier == "chocolate_bar" }
    }
    val kodein = Kodein {
        bind<Database>() with singleton { db }
        bind<EventPool>() with singleton { eventPool }
        bind<Entities>() with singleton { entities }
        bind<EventDispatcher>() with singleton { dispatcher }
    }

    val user = User.byId("chocolate_bear", kodein)

    internal fun storeWorkOrder(): Pair<String, MockDataSource<WorkOrderModel>> {
        return runBlocking {
            val id = WorkOrder.submit("chocolate_bear", 0L, "chocolate_bar", 1000L, kodein)
            val route = WorkOrderRoute("chocolate_bear", id).route
            id to db.get<WorkOrderModel>(route)
        }
    }
}