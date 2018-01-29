package nl.pocketquest.server.testhelpers

import nl.pocketquest.server.dataaccesslayer.*

class TestDB(private val initialContents: Map<List<String>, DataSource<*>>) : Database {

    private val contents = mutableMapOf<List<String>, DataSource<*>>()

    init {
        contents += initialContents
    }

    fun add(route: List<String>, dataSource: DataSource<*>) {
        contents[route] = dataSource
    }

    fun clear() = contents.clear()

    override val resolver: DataResolver = object : DataResolver {
        @SuppressWarnings("unchecked")
        override fun <T> resolve(findable: Findable<T>): DataSource<T> {
            if (!contents.contains(findable.route)) {
                contents[findable.route] = MockDataSource<T>(null)
            }
            return contents[findable.route] as DataSource<T>
        }
    }

    override fun <T> parentDataSource(parentRoute: Findable<T>, childConsumer: ChildConsumer<T>): ParentDatasource<T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}