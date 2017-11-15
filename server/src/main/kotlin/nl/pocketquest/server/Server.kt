package nl.pocketquest.server

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import nl.pocketquest.server.request.handler.impl.ResourceGatheringRequestHandler
import nl.pocketquest.server.state.State
import java.io.InputStream

fun main(args: Array<String>) {
    Server.init()
    Server.start()

    while (true) {
        Thread.sleep(10000)
    }
}

object Server {
    fun init() {
        val firebaseOptions = getFirebaseOptions()
        FirebaseApp.initializeApp(firebaseOptions)

        State.init()
    }

    fun start() {
        ResourceGatheringRequestHandler.listen()
    }

    private fun getFirebaseOptions(): FirebaseOptions {
        return FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(this.loadServiceAccount()))
                .setDatabaseUrl("https://pocket-quests.firebaseio.com")
                .build()
    }

    private fun loadServiceAccount(): InputStream {
        return javaClass.classLoader
                .getResource("service-account.json")
                .openStream()
    }

}
