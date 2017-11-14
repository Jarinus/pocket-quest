package nl.pocketquest.server

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import nl.pocketquest.server.request.resourceGathering.ResourceGatheringRequestHandler
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
        val serviceAccount = loadServiceAccount()
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://pocket-quests.firebaseio.com")
                .build()

        FirebaseApp.initializeApp(options)
    }

    fun start() {
        ResourceGatheringRequestHandler.listen()
    }

    private fun loadServiceAccount(): InputStream {
        return javaClass.classLoader
                .getResource("service-account.json")
                .openStream()
    }

}
