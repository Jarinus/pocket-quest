package nl.pocketquest.pocketquest.game.crafting

sealed class WorkOrderStatus(val lastUpdatedAt: Long) : Comparable<WorkOrderStatus>{
    data class Submitted(val submittedAt: Long) : WorkOrderStatus(submittedAt)

    data class Active(val startedAt: Long, val finishesAt: Long) : WorkOrderStatus(startedAt)

    data class Finished(val finishedAt: Long) : WorkOrderStatus(finishedAt)

    override fun compareTo(other: WorkOrderStatus) = when{
            this::class.java == other::class.java -> -lastUpdatedAt.compareTo(other.lastUpdatedAt)
            this is Finished -> 1
            this is Submitted -> -1
            this is Active  -> if(other is Finished) -1 else 1
            else ->  0
    }
}
