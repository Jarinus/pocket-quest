package nl.pocketquest.server.request.handler

import nl.pocketquest.server.request.Request

data class Response(
        val message: String?,
        val statusCode: Int
)

interface RequestHandler<T : Request> {

    fun listenPath(): String

    suspend fun handle(request: T): Response

    fun requestType(): Class<T>
}
