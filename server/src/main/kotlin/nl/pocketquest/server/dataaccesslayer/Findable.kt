package nl.pocketquest.server.dataaccesslayer

import nl.pocketquest.server.api.resource.ResourceTypeRoute

interface Findable<T> {

    val route: List<String>
    val expectedType: Class<T>

    fun subRoute(subRoute: List<String>) = of<T>(route + subRoute, expectedType)

    companion object {
        inline fun <reified T> of(route: List<String>) = of(route, T::class.java)

        fun <T> of(route: List<String>, expectedType: Class<T>): Findable<T> = object : Findable<T> {
            override val route = route
            override val expectedType = expectedType
        }
    }
}