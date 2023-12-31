package it.nicolasfarabegoli.prin.commonwears

import android.content.Context
import android.util.Log
import it.nicolasfarabegoli.prin.commonwears.wearable.DistanceFromSource
import it.nicolasfarabegoli.prin.commonwears.wearable.SignalStrengthValue
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableBehaviour
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableComm
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableDisplayInfo
import it.nicolasfarabegoli.prin.commonwears.wearable.deployment.Laptop
import it.nicolasfarabegoli.prin.commonwears.wearable.deployment.Smartphone
import it.nicolasfarabegoli.prin.commonwears.wearable.deployment.config
import it.nicolasfarabegoli.prin.commonwears.wearable.deployment.infrastructure
import it.nicolasfarabegoli.prin.commonwears.wearable.wearableBehaviourLogic
import it.nicolasfarabegoli.prin.commonwears.wearable.wearableCommLogic
import it.nicolasfarabegoli.pulverization.dsl.model.Behaviour
import it.nicolasfarabegoli.pulverization.dsl.model.Communication
import it.nicolasfarabegoli.pulverization.platforms.mqtt.MqttCommunicator
import it.nicolasfarabegoli.pulverization.platforms.mqtt.MqttReconfigurator
import it.nicolasfarabegoli.pulverization.platforms.mqtt.defaultMqttRemotePlace
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.DeploymentUnitRuntimeConfiguration
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.FailOnReconfiguration
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.ReconfigurationSuccess
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.SkipCheck
import it.nicolasfarabegoli.pulverization.runtime.dsl.pulverizationRuntime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch


/**
 * Android PulvReAKt configuration.
 */
suspend fun androidRuntimeConfig(
    context: Context,
    display: DisplayViewModel,
): DeploymentUnitRuntimeConfiguration<Unit, DistanceFromSource, SignalStrengthValue, WearableDisplayInfo, Unit> {
    val lowBatteryEvent = LowBatteryEvent(display).apply { initialize() }
    return pulverizationRuntime(config, "wearable", infrastructure) {
        WearableBehaviour() withLogic ::wearableBehaviourLogic startsOn Smartphone
        WearableComm() withLogic ::wearableCommLogic startsOn Smartphone
        WearableSensorsContainer(context) withLogic ::wearableSensorsLogic startsOn Smartphone
        WearableActuatorsContainer(display) withLogic ::wearableActuatorsLogic startsOn Smartphone

        reconfigurationRules {
            onDevice {
                lowBatteryEvent reconfigures { Behaviour movesTo Laptop }
            }
        }

        withCommunicator { MqttCommunicator(hostname = "192.168.8.1", port = 1883) }
        withReconfigurator { MqttReconfigurator(hostname = "192.168.8.1", port = 1883) }
        withRemotePlaceProvider { defaultMqttRemotePlace() }
    }
}
