package nl.pocketquest.server

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import nl.pocketquest.server.api.state.Entities
import nl.pocketquest.server.logic.request.handler.BaseHandler
import nl.pocketquest.server.logic.request.handler.RequestHandler
import nl.pocketquest.server.logic.request.handler.impl.ResourceGatheringRequestHandler
import nl.pocketquest.server.api.state.State
import nl.pocketquest.server.dataaccesslayer.Database
import nl.pocketquest.server.firebase.Firebase
import nl.pocketquest.server.logic.events.EventDispatcher
import nl.pocketquest.server.logic.events.EventHandler
import nl.pocketquest.server.logic.events.EventPool
import nl.pocketquest.server.logic.events.impl.DefaultEventDispatcher
import nl.pocketquest.server.logic.events.impl.DefaultEventPool
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringHandlers
import java.io.FileInputStream

fun main(args: Array<String>) {
    val kodeIn = Kodein {
        bind<Database>() with singleton { Firebase() }
        bind<Entities>() with singleton { State(instance()) }
        bind<EventDispatcher>() with singleton { DefaultEventDispatcher() }
        bind<EventPool>() with singleton { DefaultEventPool(instance()) }
    }
    val server = Server(kodeIn)
    server.init()
    server.start()
    while (true) {
        Thread.sleep(10000)
    }
}

class Server(private val kodein: Kodein) {

    private val requestHandlers = mutableListOf<RequestHandler<*>>(
            ResourceGatheringRequestHandler(kodein)
    )
    private val eventHandlers = listOf<EventHandler<*, *>>(

    ) + ResourceGatheringHandlers.handlers(kodein)

    private val baseHandlers = requestHandlers.map { BaseHandler(it, kodein) }

    fun init() {
        val firebaseOptions = getFirebaseOptions()
        FirebaseApp.initializeApp(firebaseOptions)
        val dispatcher = kodein.instance<EventDispatcher>()
        eventHandlers.forEach(dispatcher::register)
    }

    fun start() {
        baseHandlers.forEach {
            it.start()
        }
    }

    private fun getFirebaseOptions(): FirebaseOptions {
        return FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(this.loadServiceAccount()))
                .setDatabaseUrl("https://pocket-quests.firebaseio.com")
                .build()
    }

    private fun loadServiceAccount() = FileInputStream("service-account.json")

}
