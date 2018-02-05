package nl.pocketquest.server.testhelpers

import nl.pocketquest.server.dataaccesslayer.*

/**
 * idGenerator is used for the generation of the id of children based on the content of the data
 */
open class TestDB(initialContents: Map<List<String>, MockDataSource<*>>, var idGenerator: (Any) -> String = Any::toString) : Database {

    private val contents = mutableMapOf<List<String>, MockDataSource<*>>()

    init {
        contents += initialContents
    }

    fun add(route: List<String>, dataSource: MockDataSource<*>) {
        contents[route] = dataSource
    }

    fun <T> get(route: List<String>): MockDataSource<T> = contents[route] as MockDataSource<T>

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

    override fun <T> collection(route: Findable<T>) = object : DatabaseCollection<T>(route, resolver) {
        override fun child(key: String) = resolver.resolve(route.subRoute(listOf(key)))

        suspend override fun add(contents: T): String {
            val id = idGenerator(contents!!)
            child(id).writeAsync(contents)
            return id
        }

    }
}