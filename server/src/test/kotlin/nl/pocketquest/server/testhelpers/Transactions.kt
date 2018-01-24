package nl.pocketquest.server.testhelpers

import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.TransactionResult


open class MockDataSource<T>(inital: T?) : DataSource<T> {

    private val listeners = mutableListOf<(T?) -> Unit>()
    var content: T? = inital
        set(value) {
            listeners.forEach { it(value) }
            field = value
        }

    suspend override fun readAsync() = content

    suspend override fun writeAsync(data: T): Boolean {
        content = data
        return true
    }

    suspend override fun transaction(transformer: (T?) -> TransactionResult<T>): Boolean {
        transformer(null)
        transformer(null)
        transformer(content)
        transformer(content)
        val result = transformer(content)
        if (result.abort) {
            return false
        }
        content = result.value
        return true
    }

    override fun listen(consumer: (T?) -> Unit) {
        listeners += consumer
    }

    override fun removeListener(consumer: (T?) -> Unit) {
        listeners -= consumer
    }

    suspend override fun delete(): Boolean {
        content = null
        return true
    }
}

class FailingTransActionDataSource<T>(inital: T?) : MockDataSource<T>(inital) {

    suspend override fun transaction(transformer: (T?) -> TransactionResult<T>) = false

}