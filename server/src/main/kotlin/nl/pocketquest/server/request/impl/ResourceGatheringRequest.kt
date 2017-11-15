package nl.pocketquest.server.request.impl

import nl.pocketquest.server.request.Request

data class ResourceGatheringRequest(
        var id: String = "",
        val user_id: String = "",
        val requested_at: Long = 0,
        val resource_node_uid: String = ""
) : Request
