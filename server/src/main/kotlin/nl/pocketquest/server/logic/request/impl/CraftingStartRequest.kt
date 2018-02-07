package nl.pocketquest.server.logic.request.impl

import nl.pocketquest.server.logic.request.Request

data class CraftingStartRequest(
        override var requestID: String = "",
        val user_id: String = "",
        /**
         * Id of a specific crafting recipe: axe_1_recipe for instance
         */
        val recipe_id: String = "",
        val count: Long = 0L,
        val submitted_at: Long = 0L
) : Request

data class CraftingCancelRequest(
        override var requestID: String = "",
        val user_id: String = "",
        /**
         * Id of a workorder being executed by a user
         */
        val workorder_id: String = "",
        val submitted_at: Long = 0L

) : Request

data class CraftingClaimRequest(
        override var requestID: String = "",
        val user_id: String = "",
        /**
         * Id of a workorder being executed by a user
         */
        val workorder_id: String = "",
        val submitted_at: Long = 0L
) : Request