package nl.pocketquest.server.dataaccesslayer

interface Findable<T> {

    val route: List<String>
    val expectedType: Class<T>
}