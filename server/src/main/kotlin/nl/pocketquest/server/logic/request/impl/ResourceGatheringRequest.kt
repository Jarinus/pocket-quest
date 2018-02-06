package nl.pocketquest.server.logic.request.impl

import nl.pocketquest.server.logic.request.Request

data class ResourceGatheringRequest(
        override var requestID: String = "",
        val user_id: String = "",
        val resource_id: String = "",
        val tool_id: String = "",
        val requested_at: Long = 0,
        val resource_node_uid: String = ""
) : Request
