package nl.pocketquest.server.firebase

import com.google.firebase.database.DataSnapshot
import nl.pocketquest.server.dataaccesslayer.ChildConsumer
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.dataaccesslayer.ParentDatasource
import nl.pocketquest.server.utils.DATABASE
import nl.pocketquest.server.utils.getLogger

class FirebaseParentDataSource<T>(parentRoute: Findable<T>, childConsumer: ChildConsumer<T>) : ParentDatasource<T>(parentRoute, childConsumer) {

    override fun start() {
        DATABASE.getReference(parentRoute.route.joinToString("/"))
                .addChildEventListener(object : FBChildListener() {
                    override fun onChildAdded(snapshot: DataSnapshot?, previousChildName: String?) {
                        getLogger().info("Firebase data arrived")
                        var data: T? = null
                        try {
                            data = snapshot?.getValue(parentRoute.expectedType)
                        } catch (e: Exception) {
                            getLogger().error("Failed to read data ", e)
                        }
                        getLogger().info("Data = {} ", data)
                        val ref = snapshot?.ref
                        if (data != null && ref != null) {
                            childConsumer.consume(data, FirebaseDataSource(ref, parentRoute.expectedType))
                        }
                    }
                })
    }
}