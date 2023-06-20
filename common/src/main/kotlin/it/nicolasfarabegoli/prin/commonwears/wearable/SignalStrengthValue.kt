package it.nicolasfarabegoli.prin.commonwears.wearable

import kotlinx.serialization.Serializable

@Serializable
data class SignalStrengthValue(val rssi: Int)
