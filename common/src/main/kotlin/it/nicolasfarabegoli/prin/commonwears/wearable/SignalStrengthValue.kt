package it.nicolasfarabegoli.prin.commonwears.wearable

import kotlinx.serialization.Serializable

/**
 * [rssi] value sensed by the sensor.
 */
@Serializable
data class SignalStrengthValue(val rssi: Int)
