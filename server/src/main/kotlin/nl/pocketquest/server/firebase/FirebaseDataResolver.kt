package nl.pocketquest.server.firebase

import nl.pocketquest.server.dataaccesslayer.DataResolver
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.Findable

class FirebaseDataResolver : DataResolver {

    override fun <T> resolve(findable: Findable<T>): DataSource<T> {
        return FirebaseDataSource.findByFindable(findable)
    }
}