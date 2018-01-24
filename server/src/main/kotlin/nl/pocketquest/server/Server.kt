package nl.pocketquest.server

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import nl.pocketquest.server.logic.request.handler.BaseHandler
import nl.pocketquest.server.logic.request.handler.RequestHandler
import nl.pocketquest.server.logic.request.handler.impl.ResourceGatheringRequestHandler
import nl.pocketquest.server.api.state.State
import nl.pocketquest.server.dataaccesslayer.DatabaseConfiguration
import java.io.FileInputStream

fun main(args: Array<String>) {
    Server.init()
    Server.start()
    while (true) {
        Thread.sleep(10000)
    }
}

object Server {

    private val requestHandlers = mutableListOf<RequestHandler<*>>(
            ResourceGatheringRequestHandler()
    )
    private val baseHandlers = requestHandlers.map { BaseHandler(it) }

    fun init() {
        val firebaseOptions = getFirebaseOptions()
        FirebaseApp.initializeApp(firebaseOptions)
        DatabaseConfiguration.test = false
        State.init()
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
