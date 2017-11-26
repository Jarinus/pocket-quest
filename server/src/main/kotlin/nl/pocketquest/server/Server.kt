package nl.pocketquest.server

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.experimental.runBlocking
import nl.pocketquest.server.request.handler.BaseHandler
import nl.pocketquest.server.request.handler.RequestHandler
import nl.pocketquest.server.request.handler.impl.ResourceGatheringRequestHandler
import nl.pocketquest.server.state.State
import nl.pocketquest.server.user.Status
import nl.pocketquest.server.user.User
import nl.pocketquest.server.utils.incrementBy
import nl.pocketquest.server.utils.incrementByOrCreate
import nl.pocketquest.server.utils.readAsync
import java.io.FileInputStream
import java.io.InputStream

fun main(args: Array<String>) {
    Server.init()
    Server.start()

    while (true) {
        Thread.sleep(10000)
    }
}

object Server {

    private val requestHandlers = mutableListOf<RequestHandler<*>>(
            ResourceGatheringRequestHandler
    )
    private lateinit var baseHandlers: MutableList<BaseHandler<*>>

    fun init() {
        val firebaseOptions = getFirebaseOptions()
        FirebaseApp.initializeApp(firebaseOptions)
        State.init()
        baseHandlers = requestHandlers
                .map { BaseHandler(it) }
                .toMutableList()
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
