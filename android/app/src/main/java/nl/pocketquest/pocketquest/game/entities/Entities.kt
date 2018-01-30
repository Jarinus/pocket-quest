package nl.pocketquest.pocketquest.game.entities

import com.google.firebase.database.ServerValue
import nl.pocketquest.pocketquest.game.crafting.Recipe
import nl.pocketquest.pocketquest.game.crafting.RecipeType
import nl.pocketquest.pocketquest.utils.DATABASE
import nl.pocketquest.pocketquest.utils.readAsync
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.wtf

data class FBResourceGatherRequest(
        val resource_node_uid: String,
        val resource_id: String,
        val requested_at: Map<String, String> = ServerValue.TIMESTAMP,
        val user_id: String
)

data class FBResourceInstance(
        val type: String = "",
        val resources_left: Map<String, Int> = mapOf()
)

data class FBItem(
        val name: String = "",
        val icon: String = "",
        val tier: String = ""
)

data class FBResourceNode(
        val id: String = "",
        val icon: String = "",
        val family: String = "",
        val icon_empty: String = "",
        val tier: String = "",
        val name: String = ""
)

data class FBRecipe(
        val type: String = "",
        val duration: Long = 0,
        val required_items: HashMap<String, Int> = hashMapOf(),
        val acquired_items: HashMap<String, Int> = hashMapOf()
){
    fun toRecipe(id: String) = Recipe(id, RecipeType.valueOf(type), duration, required_items, acquired_items)
}

object Entities : AnkoLogger {
    private var items: Map<String, FBItem>? = null
    private var resource_nodes: Map<String, FBResourceNode>? = null
    private var recipes: Map<String, Recipe>? = null
    suspend fun getItem(name: String) = getItems()[name]

    suspend fun getItems() = items ?:
            DATABASE.getReference("entities/items")
                    .readAsync<HashMap<String, FBItem>>()
                    .also { items = it }

    suspend fun getResourceNodes() = resource_nodes ?:
            DATABASE.getReference("entities/resource_nodes")
                    .readAsync<HashMap<String, FBResourceNode>>()
                    .also { resource_nodes = it }

    suspend fun getRecipes() = recipes ?: DATABASE.getReference("entities/recipes")
            .readAsync<HashMap<String, FBRecipe>>()
            .mapValues { (key,fbRecipe)-> fbRecipe.toRecipe(key) }
            .also { recipes = it }
            .also { wtf("Recipes: $it") }
}
