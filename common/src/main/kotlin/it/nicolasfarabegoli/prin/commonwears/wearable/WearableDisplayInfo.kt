package it.nicolasfarabegoli.prin.commonwears.wearable

import kotlinx.serialization.Serializable

/**
 * Information of the [otherDevices] and the [myDistance] from the source.
 */
@Serializable
data class WearableDisplayInfo(val otherDevices: Map<String, Double>, val myDistance: Double)
