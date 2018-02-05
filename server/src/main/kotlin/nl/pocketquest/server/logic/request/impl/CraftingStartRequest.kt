package nl.pocketquest.server.logic.request.impl

import nl.pocketquest.server.logic.request.Request

data class CraftingStartRequest(
        override var requestID: String,
        val user_id: String,
        /**
         * Id of a specific crafting recipe: axe_1_recipe for instance
         */
        val recipe_id: String,
        val count: Long,
        val submitted_at: Long
) : Request

data class CraftingCancelRequest(
        override var requestID: String,
        val user_id: String,
        /**
         * Id of a workorder being executed by a user
         */
        val workorder_id: String,
        val submitted_at: Long

) : Request

data class CraftingClaimRequest(
        override var requestID: String,
        val user_id: String,
        /**
         * Id of a workorder being executed by a user
         */
        val workorder_id: String,
        val submitted_at: Long
) : Request