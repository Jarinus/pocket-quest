package nl.pocketquest.server

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.singleton
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.api.crafting.WorkOrder
import nl.pocketquest.server.api.entity.ResourceNode
import nl.pocketquest.server.api.resource.ResourceInstance
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
import nl.pocketquest.server.logic.request.handler.impl.WorkOrderCancelHandler
import nl.pocketquest.server.logic.request.handler.impl.WorkOrderClaimHandler
import nl.pocketquest.server.logic.request.handler.impl.WorkOrderRequestStartHandler
import nl.pocketquest.server.logic.schedule.crafting.WorkOrderEventHandlers
import nl.pocketquest.server.logic.schedule.resourcegathering.ResourceGatheringHandlers
import java.io.FileInputStream

fun main(args: Array<String>) {
    val kodeIn = Kodein {
        bind<Database>() with singleton { Firebase() }
        bind<Entities>() with singleton { State(instance()) }
        bind<EventDispatcher>() with singleton { DefaultEventDispatcher() }
        bind<EventPool>() with singleton { DefaultEventPool(instance()) }
    }
    initFirebase()
    val server = Server(kodeIn)
    server.init()

    server.start()
    runBlocking {
        WorkOrder.byId("z3GJTGEvrCc8lgZ3Ck3q4LqYi3n1", "-L4fgTgUzVjSl-0qVW1E", kodeIn)
                .also { println("Workorder ${it.recipe()}") }
    }
    kodeIn.instance<Entities>().recipe("plank_1")?.also { println("Recipe for plank_1 = $it") }
    while (true) {
        Thread.sleep(10000)
    }
}

private fun getFirebaseOptions(): FirebaseOptions {
    return FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(loadServiceAccount()))
            .setDatabaseUrl("https://pocket-quests.firebaseio.com")
            .build()
}

private fun loadServiceAccount() = FileInputStream("service-account.json")


private fun initFirebase() {
    val firebaseOptions = getFirebaseOptions()
    FirebaseApp.initializeApp(firebaseOptions)
}

class Server(private val kodein: Kodein) {

    private val requestHandlers = mutableListOf<RequestHandler<*>>(
            ResourceGatheringRequestHandler(kodein),
            WorkOrderRequestStartHandler(kodein),
            WorkOrderCancelHandler(kodein),
            WorkOrderClaimHandler(kodein)
    )
    private val eventHandlers = listOf<EventHandler<*, *>>(

    ) + ResourceGatheringHandlers.handlers(kodein) +
            WorkOrderEventHandlers.handlers(kodein)

    private val baseHandlers = requestHandlers.map { BaseHandler(it, kodein) }

    fun init() {
        val dispatcher = kodein.instance<EventDispatcher>()
        eventHandlers.forEach(dispatcher::register)
    }

    fun start() {
        baseHandlers.forEach {
            it.start()
        }
    }
}
