package it.nicolasfarabegoli.prin.commonwears.wearable

import kotlinx.serialization.Serializable

@Serializable
data class WearableDisplayInfo(val otherDevices: Map<String, Double>, val myDistance: Double)
