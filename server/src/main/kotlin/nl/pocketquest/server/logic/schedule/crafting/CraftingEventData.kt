package nl.pocketquest.server.logic.schedule.crafting

enum class WorkorderStatus {
    WORK_ORDER_CREATED,
    WORK_ORDER_MAY_NEED_TO_BE_SCHEDULED,
    WORK_ORDER_SCHEDULED,
    WORK_ORDER_ONE_COMPLETED,
    WORK_ORDER_CLAIMED,
    WORK_ORDER_DEACTIVATED,
    WORK_ORDER_CANCELLED,
}

data class WorkOrderData(
        val userID: String,
        val workOrderID: String
)

data class WorkOrderProcessData(
        val userID: String,
        val workOrderID: String,
        val interval: Long
)

data class WorkOrderCreationData(
        val userID: String,
        val recipeID: String,
        val count: Long
)

data class WorkOrderUserData(
        val userID: String
)
