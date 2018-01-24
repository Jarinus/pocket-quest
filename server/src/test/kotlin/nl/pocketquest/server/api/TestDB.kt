package nl.pocketquest.server.api

import nl.pocketquest.server.dataaccesslayer.*
import nl.pocketquest.server.utils.getLogger

class TestDB(private val contents: Map<List<String>, DataSource<*>>) : Database {

    override val resolver: DataResolver = object : DataResolver {
        @SuppressWarnings("unchecked")
        override fun <T> resolve(findable: Findable<T>): DataSource<T> {
//            getLogger().info("{}", findable.route)
//            getLogger().info("{}", contents[findable.route])
            return contents[findable.route] as DataSource<T>
        }
    }

    override fun <T> parentDataSource(parentRoute: Findable<T>, childConsumer: ChildConsumer<T>): ParentDatasource<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}