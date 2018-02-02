package nl.pocketquest.pocketquest.game.crafting

data class WorkOrder(
        val id: String,
        val recipeId: String,
        val count: Int,
        var status: WorkOrderStatus
)
