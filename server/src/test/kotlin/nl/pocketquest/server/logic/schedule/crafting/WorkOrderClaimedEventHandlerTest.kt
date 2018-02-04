package nl.pocketquest.server.logic.schedule.crafting

import com.github.salomonbrys.kodein.factory
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.lazy
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.crafting.WorkOrder
import nl.pocketquest.server.api.crafting.WorkOrderTest
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.logic.events.Event
import org.junit.Assert.*
import org.junit.Test

class WorkOrderClaimedEventHandlerTest : WorkOrderBaseTest() {


    @Test
    fun shouldGiveItems() {
        db.clear()
        val (workorderId, workorder) = storeWorkOrder()
        runBlocking {
            workorder.content!!.completed = 2
            workorder.content!!.count = 5

            WorkOrderClaimedEventHandler(kodein).handle(
                    Event.of(WorkorderStatus.WORK_ORDER_CLAIMED, WorkOrderData(
                            "chocolate_bear", workorderId
                    ), 0L))
            assertEquals(null, workorder.content)
            assertEquals(4L, user.inventory.item("chocolate").count())
            assertEquals(9L, user.inventory.item("cacao").count())
            assertEquals(6L, user.inventory.item("milk").count())
        }
    }
}