package nl.pocketquest.server.firebase

import nl.pocketquest.server.dataaccesslayer.*

class Firebase : Database {
    override val resolver = FirebaseDataResolver()

    override fun <T> parentDataSource(parentRoute: Findable<T>, childConsumer: ChildConsumer<T>) = FirebaseParentDataSource(parentRoute, childConsumer)
}