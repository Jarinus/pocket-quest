package nl.pocketquest.server.dataaccesslayer

interface DataSource<T> {

    suspend fun readAsync(): T?

    /**
     * @throws Expception if the reading fails
     * @return true if the value has been written
     */
    suspend fun writeAsync(data: T): Boolean

    suspend fun transaction(transformer: (T?) -> TransactionResult<T>): Boolean

    fun listen(consumer: (T?) -> Unit)

    fun removeListener(consumer: (T?) -> Unit)

    suspend fun delete(): Boolean
}
