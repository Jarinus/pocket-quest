package nl.pocketquest.pocketquest.game.entities

/**
 * Created by Laurens on 31-10-2017.
 */
class FBResourceNode(
        var id: String? = null,
        var icon: String? = null,
        var family: String? = null,
        var tier: Int? = null,
        var name: String? = null)

class FBResourceInstance(
        var id: String? = null,
        var type: String? = null,
        var resources_left: Map<String, Int>? = null
)
