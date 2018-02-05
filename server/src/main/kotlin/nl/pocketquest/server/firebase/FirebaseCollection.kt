package nl.pocketquest.server.firebase

import nl.pocketquest.server.dataaccesslayer.DataResolver
import nl.pocketquest.server.dataaccesslayer.DataSource
import nl.pocketquest.server.dataaccesslayer.DatabaseCollection
import nl.pocketquest.server.dataaccesslayer.Findable
import nl.pocketquest.server.utils.DATABASE

class FirebaseCollection<T>(route: Findable<T>, resolver: DataResolver) : DatabaseCollection<T>(route, resolver) {

    override fun child(key: String) = resolver.resolve(route.subRoute(listOf(key)))

    override suspend fun add(contents: T): String {
        val id = DATABASE.getReference(route.route.joinToString("/")).push().key
        child(id).writeAsync(contents)
        return id
    }
}