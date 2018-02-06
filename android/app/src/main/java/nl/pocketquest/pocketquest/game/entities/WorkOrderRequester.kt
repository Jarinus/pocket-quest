package nl.pocketquest.pocketquest.game.entities

import com.google.firebase.database.ServerValue
import nl.pocketquest.pocketquest.game.player.FBWorkOrderModel
import nl.pocketquest.pocketquest.utils.DATABASE

data class CraftingStartRequest(
        val user_id: String,
        /**
         * Id of a specific crafting recipe: axe_1_recipe for instance
         */
        val recipe_id: String,
        val count: Int,
        val submitted_at: MutableMap<String, String>
)

data class CraftingCancelRequest(
        val user_id: String,
        /**
         * Id of a workorder being executed by a user
         */
        val workorder_id: String,
        val submitted_at: MutableMap<String, String>
)

data class CraftingClaimRequest(
        val user_id: String,
        /**
         * Id of a workorder being executed by a user
         */
        val workorder_id: String,
        val submitted_at: MutableMap<String, String>
)

object WorkOrderRequester {
    fun claimWorkorder(user_id: String, workorderId: String) {
        DATABASE.getReference("requests/crafting_claim").push()
                .setValue(
                        CraftingClaimRequest(
                                user_id = user_id,
                                workorder_id = workorderId,
                                submitted_at = ServerValue.TIMESTAMP
                        )
                )
    }

    fun cancelWorkorder(user_id: String, workOrderId: String) {
        DATABASE.getReference("requests/crafting_cancel").push()
                .setValue(
                        CraftingCancelRequest(
                                user_id = user_id,
                                workorder_id = workOrderId,
                                submitted_at = ServerValue.TIMESTAMP
                        )
                )
    }

    fun startWorkOrder(user_id: String, workOrder: FBWorkOrderModel) {
        DATABASE.getReference("requests/crafting_start").push()
                .setValue(
                        CraftingStartRequest(
                                user_id = user_id,
                                recipe_id = workOrder.recipe,
                                submitted_at = ServerValue.TIMESTAMP,
                                count = workOrder.count
                        )
                )
    }
}
