package nl.pocketquest.server.dataaccesslayer

interface DataResolver {

    fun <T> resolve(findable: Findable<T>): DataSource<T>
}