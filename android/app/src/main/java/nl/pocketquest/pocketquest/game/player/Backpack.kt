package nl.pocketquest.pocketquest.game.player

import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.whenLoggedIn

/**
 * Created by Laurens on 13-11-2017.
 */
class Backpack {

    init {
        whenLoggedIn {
//            DATABASE.getReference("user_items/${it.uid}/backpack").addValueEventListener()

        }
    }


}
