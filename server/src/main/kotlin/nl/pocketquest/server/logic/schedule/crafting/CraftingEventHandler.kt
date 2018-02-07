package nl.pocketquest.server.logic.schedule.crafting

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.crafting.WorkOrder
import nl.pocketquest.server.api.crafting.WorkOrderQueue
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.api.user.User
import nl.pocketquest.server.api.user.updateUser
import nl.pocketquest.server.logic.events.Event
import nl.pocketquest.server.logic.events.EventHandler
import nl.pocketquest.server.logic.events.EventPool
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringGainHandler
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringStartHandler
import nl.pocketquest.server.utils.getLogger
import java.util.concurrent.TimeUnit

object WorkOrderEventHandlers {
    fun handlers(kodein: Kodein) = listOf(
            WorkOrderCancelledEventHandler(kodein),
            WorkOrderClaimedEventHandler(kodein),
            WorkOrderCreatedEventHandler(kodein),
            WorkOrderMayNeedToBeScheduledEventHandler(kodein),
            WorkOrderScheduledEventHandler(kodein),
            WorkOrderOneCompletedEventHandler(kodein),
            WorkOrderDeactivatedEventHandler(kodein)
    )
}

abstract class WorkOrderEventHandler<T>(val kodein: Kodein) : EventHandler<WorkorderStatus, T> {
    override val typeClass = WorkorderStatus::class.java
}

class WorkOrderCancelledEventHandler(kodein: Kodein) : WorkOrderEventHandler<WorkOrderData>(kodein) {
    override val dataClass = WorkOrderData::class.java
    override fun isRelevant(type: WorkorderStatus) = type == WorkorderStatus.WORK_ORDER_CANCELLED

    suspend override fun handle(event: Event<WorkorderStatus, WorkOrderData>) {
        kodein.instance<EventPool>().also {
            it.submit(
                    Event.of(WorkorderStatus.WORK_ORDER_DEACTIVATED,
                            WorkOrderDeactivationData(event.data.userID, event.data.workOrderID, true)
                            , event.scheduledFor)
            )
        }
    }
}

class WorkOrderClaimedEventHandler(kodein: Kodein) : WorkOrderEventHandler<WorkOrderData>(kodein) {
    override val dataClass = WorkOrderData::class.java
    override fun isRelevant(type: WorkorderStatus) = type == WorkorderStatus.WORK_ORDER_CLAIMED

    suspend override fun handle(event: Event<WorkorderStatus, WorkOrderData>) {
        val workOrder = WorkOrder.byId(event.data.userID, event.data.workOrderID, kodein)
        val recipeID = workOrder.recipe() ?: throw IllegalStateException("No recipe found for workOrder")
        val craftingRecipe = kodein.instance<Entities>().recipe(recipeID) ?: throw IllegalStateException("No recipe found for workOrder")
        val completed = workOrder.completed() ?: 0L
        val total = workOrder.count() ?: 0L
        val user = User.byId(event.data.userID, kodein)
        giveItems(completed, user, craftingRecipe.acquiredItems)
        giveItems(total - completed, user, craftingRecipe.requiredItems)
        workOrder.delete()
    }

    private suspend fun giveItems(multiplier: Long, user: User, items: Map<String, Long>) {
        if (multiplier == 0L) return
        val inventory = user.inventory
        items.forEach { (item, amount) ->
            inventory.item(item).give(amount * multiplier)
        }
    }
}

class WorkOrderScheduledEventHandler(kodein: Kodein) : WorkOrderEventHandler<WorkOrderData>(kodein) {
    override val dataClass = WorkOrderData::class.java
    override fun isRelevant(type: WorkorderStatus) = type == WorkorderStatus.WORK_ORDER_SCHEDULED

    suspend override fun handle(event: Event<WorkorderStatus, WorkOrderData>) {
        val workOrder = WorkOrder.byId(event.data.userID, event.data.workOrderID, kodein)
        val user = User.byId(event.data.userID, kodein)
        val interval = workOrder.recipe()?.let {
            kodein.instance<Entities>()
                    .recipe(it)
                    ?.duration
        }
                ?: return
        scheduleWorkOrderCompletion(workOrder, event, interval)
    }

    private suspend fun scheduleWorkOrderCompletion(workOrder: WorkOrder, event: Event<WorkorderStatus, WorkOrderData>, interval: Long) {
        val intervalMs = TimeUnit.SECONDS.toMillis(interval)
        val workOrderCount = workOrder.count() ?: return
        workOrder.updateObject(
                active = true,
                startedAt = event.scheduledFor,
                finishedAt = workOrderCount * intervalMs + event.scheduledFor
        )
        kodein.instance<EventPool>()
                .submit(
                        Event.of(
                                WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                                WorkOrderProcessData(
                                        event.data.userID,
                                        event.data.workOrderID,
                                        intervalMs
                                ),
                                event.scheduledFor + intervalMs
                        )
                )
    }
}

class WorkOrderOneCompletedEventHandler(kodein: Kodein) : WorkOrderEventHandler<WorkOrderProcessData>(kodein) {
    override val dataClass = WorkOrderProcessData::class.java
    override fun isRelevant(type: WorkorderStatus) = type == WorkorderStatus.WORK_ORDER_ONE_COMPLETED

    suspend override fun handle(event: Event<WorkorderStatus, WorkOrderProcessData>) {
        val workOrder = WorkOrder.byId(event.data.userID, event.data.workOrderID, kodein)
        if (!workOrder.exists()) return
        if (workOrder.addOneCompleted() && !workOrder.finished()) {
            kodein.instance<EventPool>()
                    .submit(
                            Event.of(WorkorderStatus.WORK_ORDER_ONE_COMPLETED,
                                    event.data,
                                    event.scheduledFor + event.data.interval)
                    )
        } else {
            kodein.instance<EventPool>()
                    .submit(
                            Event.of(WorkorderStatus.WORK_ORDER_DEACTIVATED,
                                    WorkOrderDeactivationData(
                                            event.data.userID,
                                            event.data.workOrderID
                                    ),
                                    event.scheduledFor)
                    )
        }
    }
}

class WorkOrderDeactivatedEventHandler(kodein: Kodein) : WorkOrderEventHandler<WorkOrderDeactivationData>(kodein) {
    override val dataClass = WorkOrderDeactivationData::class.java
    override fun isRelevant(type: WorkorderStatus) = type == WorkorderStatus.WORK_ORDER_DEACTIVATED

    suspend override fun handle(event: Event<WorkorderStatus, WorkOrderDeactivationData>) {
        val workOrder = WorkOrder.byId(event.data.userID, event.data.workOrderID, kodein)
        workOrder.updateObject(
                active = false,
                finished = true
        )
        updateUser(event.data.userID, kodein) {
            decrementCraftingCount()
        }
        WorkOrderQueue.of(event.data.userID, kodein)
                .delete(event.data.workOrderID)
        kodein.instance<EventPool>().submit(
                Event.of(
                        WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                        WorkOrderUserData(event.data.userID),
                        event.scheduledFor)
        )
        if (event.data.claim) {
            kodein.instance<EventPool>().submit(
                    Event.of(WorkorderStatus.WORK_ORDER_CLAIMED, WorkOrderData(
                            event.data.userID,
                            event.data.workOrderID
                    ), event.scheduledFor)
            )
        }
    }
}

class WorkOrderCreatedEventHandler(kodein: Kodein) : WorkOrderEventHandler<WorkOrderCreationData>(kodein) {
    override val dataClass = WorkOrderCreationData::class.java
    override fun isRelevant(type: WorkorderStatus) = type == WorkorderStatus.WORK_ORDER_CREATED

    suspend override fun handle(event: Event<WorkorderStatus, WorkOrderCreationData>) {
        if (!takeResources(event)) {
            return
        }
        val queue = WorkOrderQueue.of(event.data.userID, kodein)
        val workOrderId = WorkOrder.submit(event.data.userID, event.data.count, event.data.recipeID, event.scheduledFor, kodein)
        queue.submit(workOrderId, event.scheduledFor)
        kodein.instance<EventPool>().submit(
                Event.of(
                        WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                        WorkOrderUserData(event.data.userID),
                        event.scheduledFor)
        )
    }

    private suspend fun takeResources(event: Event<WorkorderStatus, WorkOrderCreationData>): Boolean {
        val inventory = User.byId(event.data.userID, kodein).inventory
        val requiredItems = kodein.instance<Entities>()
                .recipe(event.data.recipeID)
                ?.requiredItems
                ?.mapValues { it.value * event.data.count }
                ?: return false
        return inventory.hasAll(requiredItems) && inventory.takeAllOrNothing(requiredItems)
    }
}

class WorkOrderMayNeedToBeScheduledEventHandler(kodein: Kodein) : WorkOrderEventHandler<WorkOrderUserData>(kodein) {
    override val dataClass = WorkOrderUserData::class.java
    override fun isRelevant(type: WorkorderStatus) = type == WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED

    suspend override fun handle(event: Event<WorkorderStatus, WorkOrderUserData>) {
        val queue = WorkOrderQueue.of(event.data.userID, kodein)
        val user = User.byId(event.data.userID, kodein)
        if (!user.incrementCraftingCount()) return
        val workOrderId = queue.takeOldest()
        if (workOrderId == null) {
            user.decrementCraftingCount()
            return
        }
        kodein.instance<EventPool>().submit(
                Event.of(
                        WorkorderStatus.WORK_ORDER_SCHEDULED,
                        WorkOrderData(
                                event.data.userID,
                                workOrderId
                        ),
                        event.scheduledFor
                )
        )
        if (user.hasCraftingCountAvailable()) {
            kodein.instance<EventPool>().submit(
                    Event.of(
                            WorkorderStatus.WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
                            event.data,
                            event.scheduledFor
                    )
            )
        }
    }
}
