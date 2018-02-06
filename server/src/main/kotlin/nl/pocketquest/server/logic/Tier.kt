package nl.pocketquest.server.logic

import java.lang.Double
import kotlin.math.ceil
import kotlin.math.roundToInt

class Tier (val tierTool: String) : Comparable<Tier> {

    private val numeric = Double.parseDouble(tierTool)

    override fun compareTo(other: Tier) = numeric.compareTo(other.numeric)
}



class TierInterval{

    fun calcInterval(tierTool: String, tierResourceNode: String, interval: Int): Int{
        val interval = interval * 1000
        val toolMultiplier = tierTool.toInt()*0.2+0.8
        val resourceMultiplier = tierResourceNode.toInt()*0.2+0.8
        val endInterval = resourceMultiplier*interval/toolMultiplier
        return ceil(endInterval).toInt()
    }
}