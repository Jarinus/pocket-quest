package nl.pocketquest.server.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.dataaccesslayer.TransactionResult
import nl.pocketquest.server.utils.*

class FirebaseDataSource<T>(private val reference: DatabaseReference, private val expectedType: Class<T>) : DataSource<T> {

    private val listeners = mutableMapOf<(T?) -> Unit, ValueEventListener>()


    suspend override fun readAsync() = reference.readAsync<T>(expectedType)

    suspend override fun writeAsync(data: T) = reference.writeAsync(data)

    suspend override fun transaction(transformer: (T?) -> TransactionResult<T>) = reference.transaction(expectedType, transformer)

    suspend override fun delete() = reference.remove()

    override fun listen(consumer: (T?) -> Unit) {
        val listener: ValueEventListener = object : FBValueListener() {
            override fun onDataChange(snapshot: DataSnapshot?) {
                snapshot?.getValue(expectedType).also(consumer)
            }
        }
        reference.addValueEventListener(listener)
        listeners[consumer] = listener
    }

    override fun removeListener(consumer: (T?) -> Unit) {
        listeners[consumer]?.also {
            reference.removeEventListener(it)
        }
    }

    companion object {
        private fun firebasePath(findable: Findable<*>) = findable.route.joinToString("/")

        fun <T> findByFindable(findable: Findable<T>) = FirebaseDataSource(
                DATABASE.getReference(firebasePath(findable)),
                findable.expectedType
        )
    }
}