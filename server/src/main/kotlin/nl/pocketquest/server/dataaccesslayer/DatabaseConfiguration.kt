package nl.pocketquest.server.dataaccesslayer

import com.google.firebase.database.FirebaseDatabase
import nl.pocketquest.server.firebase.Firebase
import nl.pocketquest.server.firebase.FirebaseDataResolver

object DatabaseConfiguration {

    var test = false
    var testDB: Database? = null

    private val normalDB = Firebase()
    val database: Database = Firebase()
        get() {
            return if (!test) field else testDB!!
        }
}