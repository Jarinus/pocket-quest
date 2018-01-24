package nl.pocketquest.server.firebase

import com.google.firebase.database.DataSnapshot
import nl.pocketquest.server.dataaccesslayer.ChildConsumer
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.dataaccesslayer.ParentDatasource
import nl.pocketquest.server.utils.DATABASE

class FirebaseParentDataSource<T>(parentRoute: Findable<T>, childConsumer: ChildConsumer<T>) : ParentDatasource<T>(parentRoute, childConsumer) {

    override fun start() {
        DATABASE.getReference(parentRoute.route.joinToString("/"))
                .addChildEventListener(object : FBChildListener() {
                    override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
                        val data = snapshot?.getValue(parentRoute.expectedType)
                        val ref = snapshot?.ref
                        if (data != null && ref != null) {
                            childConsumer.consume(data, FirebaseDataSource(ref, parentRoute.expectedType))
                        }
                    }
                })
    }
}