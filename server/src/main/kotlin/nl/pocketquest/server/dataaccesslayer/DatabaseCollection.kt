package nl.pocketquest.server.dataaccesslayer

abstract class DatabaseCollection<T>(val route: Findable<T>, val resolver: DataResolver) {

    abstract fun child(key: String): DataSource<T>

    /**
     * Returns the location where the new data is stored
     * Calling the child function with this key returns the @see DataSource representing the data
     */
    abstract suspend fun add(contents: T): String
}