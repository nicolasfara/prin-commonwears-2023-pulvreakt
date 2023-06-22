package it.nicolasfarabegoli.prin.commonwears.wearable.deployment

import it.nicolasfarabegoli.prin.commonwears.wearable.DistanceFromSource
import it.nicolasfarabegoli.prin.commonwears.wearable.FakeActuators
import it.nicolasfarabegoli.prin.commonwears.wearable.FakeSensors
import it.nicolasfarabegoli.prin.commonwears.wearable.SignalStrengthValue
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableBehaviour
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableComm
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableDisplayInfo
import it.nicolasfarabegoli.prin.commonwears.wearable.wearableBehaviourLogic
import it.nicolasfarabegoli.prin.commonwears.wearable.wearableCommLogic
import it.nicolasfarabegoli.pulverization.dsl.model.Actuators
import it.nicolasfarabegoli.pulverization.dsl.model.Behaviour
import it.nicolasfarabegoli.pulverization.dsl.model.Capability
import it.nicolasfarabegoli.pulverization.dsl.model.Communication
import it.nicolasfarabegoli.pulverization.dsl.model.Sensors
import it.nicolasfarabegoli.pulverization.dsl.model.SystemSpecification
import it.nicolasfarabegoli.pulverization.dsl.pulverizationSystem
import it.nicolasfarabegoli.pulverization.platforms.mqtt.MqttCommunicator
import it.nicolasfarabegoli.pulverization.platforms.mqtt.MqttReconfigurator
import it.nicolasfarabegoli.pulverization.platforms.mqtt.defaultMqttRemotePlace
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.DeploymentUnitRuntimeConfiguration
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.Host
import it.nicolasfarabegoli.pulverization.runtime.dsl.pulverizationRuntime
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

/**
 * TODO.
 */
suspend fun runtimeConfig(
    config: SystemSpecification,
): DeploymentUnitRuntimeConfiguration<Unit, DistanceFromSource, SignalStrengthValue, WearableDisplayInfo, Unit> {
    return pulverizationRuntime(config, "wearable", infrastructure) {
        WearableBehaviour() withLogic ::wearableBehaviourLogic startsOn Smartphone
        WearableComm() withLogic ::wearableCommLogic startsOn Smartphone
        FakeSensors() startsOn Smartphone
        FakeActuators() startsOn Smartphone

        withCommunicator { MqttCommunicator(hostname = "192.168.8.1", port = 1883) }
        withReconfigurator { MqttReconfigurator(hostname = "192.168.8.1", port = 1883) }
        withRemotePlaceProvider { defaultMqttRemotePlace() }
    }
}
