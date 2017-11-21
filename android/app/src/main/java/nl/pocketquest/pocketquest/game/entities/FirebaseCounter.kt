package nl.pocketquest.pocketquest.game.entities

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

/**
 * Used for objects which have children which are integers. Each change fires the current state of the object
 */
class FirebaseCounter(private val reference: DatabaseReference) {

    private val counts = mutableMapOf<String, Long>()
    private val listeners = mutableListOf<(Map<String, Long>) -> Unit>()

    init {
        addResourceCountListener()
    }

    private fun addResourceCountListener() {
        reference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) = Unit

            override fun onChildMoved(snapshot: DataSnapshot?, oldKey: String?) = Unit

            override fun onChildChanged(snapshot: DataSnapshot?, oldKey: String?) {
                snapshot?.also(this@FirebaseCounter::updateCounts)
            }

            override fun onChildAdded(snapshot: DataSnapshot?, oldKey: String?) {
                snapshot?.also(this@FirebaseCounter::updateCounts)
            }

            override fun onChildRemoved(snapshot: DataSnapshot?) {
                counts -= snapshot?.key ?: return
                notifyListeners()
            }
        })
    }

    fun updateCounts(snapshot: DataSnapshot) {
        counts[snapshot.key] = snapshot.getValue(Long::class.java) ?: return
        notifyListeners()
    }

    fun notifyListeners() = listeners.forEach { it(counts) }

    fun addListener(listener: (Map<String, Long>) -> Unit) = listeners.add(listener)
}
