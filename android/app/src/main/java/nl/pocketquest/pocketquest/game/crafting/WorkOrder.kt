package nl.pocketquest.pocketquest.game.crafting

data class WorkOrder(
        private val recipeId: String,
        private val count: Int,
        private var status: WorkOrderStatus
)
