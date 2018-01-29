package nl.pocketquest.server.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

open class FBValueListener : ValueEventListener {
    override fun onCancelled(error: DatabaseError?) {
    }

    override fun onDataChange(snapshot: DataSnapshot?) {
    }
}
