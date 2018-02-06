package nl.pocketquest.server.testhelpers

import nl.pocketquest.server.api.entity.*
import nl.pocketquest.server.api.state.Entities

open class TestEntities : Entities {
    override fun gatheringToolFamily(identifier: String): GatheringToolFamily? =null
    override fun item(identifier: String): Item? = null
    override fun resourceNode(identifier: String): ResourceNode? = null
    override fun recipe(identifier: String): CraftingRecipe? = null
    override fun resourceNodeFamily(identifier: String): ResourceNodeFamily? = null
}