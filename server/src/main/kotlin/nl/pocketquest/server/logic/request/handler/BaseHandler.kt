package nl.pocketquest.server.logic.request.handler

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import nl.pocketquest.server.dataaccesslayer.ChildConsumer
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.logic.request.Request
import nl.pocketquest.server.utils.getLogger


class BaseHandler<T : Request>(private val handler: RequestHandler<T>, private val kodein: Kodein) : ChildConsumer<T> {

    fun start() {
        kodein.instance<Database>()
                .parentDataSource(handler.route, this)
                .start()
    }

    override fun consume(readOnlyData: T, dataSource: DataSource<T>) {
        async(CommonPool) {
            try {
                handler.handle(readOnlyData, dataSource)
                        .also { handleResponse(it) }
                dataSource.delete()
            } catch (e: Exception) {
                getLogger().error("Exception while handling request", e)
            }
        }
    }

    private suspend fun handleResponse(response: Response) {
        getLogger().info("received response {}", response)
    }
}