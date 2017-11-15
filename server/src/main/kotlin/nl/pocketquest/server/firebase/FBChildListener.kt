package nl.pocketquest.server.firebase

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

open class FBChildListener : ChildEventListener {
    override fun onCancelled(error: DatabaseError?) {
    }

    override fun onChildMoved(snapshot: DataSnapshot?, previousChildName: String?) {
    }

    override fun onChildChanged(snapshot: DataSnapshot?, previousChildName: String?) {
    }

    override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
    }

    override fun onChildRemoved(snapshot: DataSnapshot?) {
    }
}
