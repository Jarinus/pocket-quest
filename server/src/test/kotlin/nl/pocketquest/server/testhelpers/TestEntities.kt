package nl.pocketquest.server.testhelpers

import nl.pocketquest.server.api.entity.CraftingRecipe
import nl.pocketquest.server.api.entity.Item
import nl.pocketquest.server.api.entity.ResourceNode
import nl.pocketquest.server.api.entity.ResourceNodeFamily
import nl.pocketquest.server.api.state.Entities

open class TestEntities : Entities {
    override fun item(identifier: String): Item? = null
    override fun resourceNode(identifier: String): ResourceNode? = null
    override fun recipe(identifier: String): CraftingRecipe? = null
    override fun resourceNodeFamily(identifier: String): ResourceNodeFamily? = null
}