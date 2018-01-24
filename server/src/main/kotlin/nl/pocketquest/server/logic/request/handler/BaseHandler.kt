package nl.pocketquest.server.logic.request.handler

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.server.dataaccesslayer.ChildConsumer
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.DatabaseConfiguration
import nl.pocketquest.server.logic.request.Request
import nl.pocketquest.server.utils.getLogger


class BaseHandler<T : Request>(private val handler: RequestHandler<T>) : ChildConsumer<T> {

    fun start() {
        DatabaseConfiguration.database.parentDataSource(handler.route, this).start()
    }

    override fun consume(readOnlyData: T, dataSource: DataSource<T>) {
        async(CommonPool) {
            try {
                handler.handle(readOnlyData, dataSource)
                        .also { handleResponse(it) }
            } catch (e: Exception) {
                getLogger().error("Exception while handling request", e)
            }
        }
    }

    private suspend fun handleResponse(response: Response) {
        getLogger().info("received response {}", response)
    }
}