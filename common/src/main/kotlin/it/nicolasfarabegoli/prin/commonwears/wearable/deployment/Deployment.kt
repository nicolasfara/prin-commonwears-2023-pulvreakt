package it.nicolasfarabegoli.prin.commonwears.wearable.deployment

import it.nicolasfarabegoli.pulverization.dsl.model.Actuators
import it.nicolasfarabegoli.pulverization.dsl.model.Behaviour
import it.nicolasfarabegoli.pulverization.dsl.model.Capability
import it.nicolasfarabegoli.pulverization.dsl.model.Communication
import it.nicolasfarabegoli.pulverization.dsl.model.Sensors
import it.nicolasfarabegoli.pulverization.dsl.pulverizationSystem
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.Host
import kotlinx.serialization.Serializable

/**
 * TODO.
 */
@Serializable
object EmbeddedDevice : Capability

/**
 * TODO.
 */
@Serializable
object HighCpu : Capability

/**
 * TODO.
 */
@Serializable
object Smartphone : Host {
    override val hostname = "android"
    override val capabilities = setOf(EmbeddedDevice)
}

/**
 * TODO.
 */
@Serializable
object Laptop : Host {
    override val hostname = "alice"
    override val capabilities = setOf(HighCpu)
}

/**
 * TODO.
 */
val infrastructure = setOf(Smartphone, Laptop)

/**
 * TODO.
 */
val config = pulverizationSystem {
    device("wearable") {
        Behaviour and Communication deployableOn setOf(EmbeddedDevice, HighCpu)
        Sensors and Actuators deployableOn setOf(EmbeddedDevice)
    }
}
