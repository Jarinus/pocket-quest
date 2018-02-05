package nl.pocketquest.server.dataaccesslayer

interface Database {
    val resolver: DataResolver
    fun <T> parentDataSource(parentRoute: Findable<T>, childConsumer: ChildConsumer<T>): ParentDatasource<T>
    fun <T> collection(route: Findable<T>): DatabaseCollection<T>
}