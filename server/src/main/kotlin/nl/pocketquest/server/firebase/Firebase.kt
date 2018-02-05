package nl.pocketquest.server.firebase

import nl.pocketquest.server.dataaccesslayer.*

class Firebase : Database {
    override fun <T> collection(route: Findable<T>) = FirebaseCollection<T>(route, resolver)

    override val resolver = FirebaseDataResolver()

    override fun <T> parentDataSource(parentRoute: Findable<T>, childConsumer: ChildConsumer<T>) = FirebaseParentDataSource(parentRoute, childConsumer)
}