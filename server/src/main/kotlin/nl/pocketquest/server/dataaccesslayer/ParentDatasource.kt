package nl.pocketquest.server.dataaccesslayer

/**
 * Listens for arrival of child of this parent. When a child arrives the childConsumer is invoked.
 *
 * */
abstract class ParentDatasource<T>(val parentRoute: Findable<T>, val childConsumer: ChildConsumer<T>) {

    abstract fun start()
}

interface ChildConsumer<T> {

    /**
     * The read only data is the fastest. The datasource can be used to modify the child
     */
    fun consume(readOnlyData: T, dataSource: DataSource<T>)
}