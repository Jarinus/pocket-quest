package nl.pocketquest.pocketquest.game.crafting

sealed class WorkOrderStatus {
    data class Submitted(val submittedAt: Long) : WorkOrderStatus()

    data class Active(val startedAt: Long, val finishesAt: Long) : WorkOrderStatus()

    data class Finished(val finishedAt: Long) : WorkOrderStatus()

    fun compare(other: WorkOrderStatus): Int {
        return when (this) {
            is Finished -> {
                when (other) {
                    is Finished -> when {
                        this.finishedAt < other.finishedAt -> 1
                        this.finishedAt == other.finishedAt -> 0
                        else -> -1
                    }
                    else -> 1
                }
            }

            is Active -> {
                when (other) {
                    is Finished -> -1
                    is Active -> when {
                        this.startedAt < other.startedAt -> 1
                        this.startedAt == other.startedAt -> 0
                        else -> -1
                    }
                    else -> 1
                }
            }

            is Submitted -> {
                when (other) {
                    is Submitted -> when {
                        this.submittedAt < other.submittedAt -> 1
                        this.submittedAt == other.submittedAt -> 0
                        else -> -1
                    }
                    else -> -1
                }
            }
        }
    }
}
