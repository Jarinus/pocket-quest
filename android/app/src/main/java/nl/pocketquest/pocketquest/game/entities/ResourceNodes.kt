package nl.pocketquest.pocketquest.game.entities

import nl.pocketquest.pocketquest.utils.DATABASE

/**
 * Created by Laurens on 6-11-2017.
 */
object ResourceNodes {
    init {
        DATABASE.getReference("/entities")

    }
}
