package nl.pocketquest.server.logic.request.handler

import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.logic.request.Request

data class Response(
        val message: String?,
        val statusCode: Int
)

interface RequestHandler<T : Request> {


    suspend fun handle(request: T, requestReference: DataSource<T>): Response

    /**
     * The route underneath which child requests will arrive. Requests have type T
     */
    val route: Findable<T>
}
