package nl.pocketquest.pocketquest.game.crafting

sealed class WorkOrderStatus {
    data class Submitted(val submittedAt: Long) : WorkOrderStatus()

    data class Active(val startedAt: Long, val finishesAt: Long) : WorkOrderStatus()

    class Finished : WorkOrderStatus()
}
