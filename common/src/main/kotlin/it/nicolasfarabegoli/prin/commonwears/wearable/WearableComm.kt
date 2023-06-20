package it.nicolasfarabegoli.prin.commonwears.wearable

import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.Communication
import it.nicolasfarabegoli.pulverization.runtime.componentsref.BehaviourRef
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.koin.core.component.inject

@Serializable
data class DistanceFromSource(val deviceId: String, val distance: Double)

class WearableComm : Communication<DistanceFromSource> {
    override val context: Context by inject()

    private val mqttClient = MqttClient("tcp://192.168.8.1:1883", MqttClient.generateClientId(), MemoryPersistence())
    private val commTopic = "communication"
    private val defaultQoS = 2

    override suspend fun initialize() {
        val mqttConnectionOption = MqttConnectOptions().apply {
            userName = ""
            password = "".toCharArray()
        }

        withContext(Dispatchers.IO) {
            mqttClient.connect(mqttConnectionOption)
            mqttClient.subscribe(commTopic, defaultQoS)
        }
    }

    override suspend fun finalize() {
        mqttClient.disconnect()
    }

    override fun receive(): Flow<DistanceFromSource> = callbackFlow<DistanceFromSource> {
        val callback = object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                cancel(CancellationException("Connection lost", cause))
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                requireNotNull(message) { "Mqtt message cannot be null" }
                trySend(Json.decodeFromString(message.payload.decodeToString()))
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
        }
        mqttClient.setCallback(callback)
        awaitClose { mqttClient.unsubscribe(commTopic) }
    }.filterNot { it.deviceId == context.deviceID }

    override suspend fun send(payload: DistanceFromSource) {
        mqttClient.publish(commTopic, Json.encodeToString(payload).encodeToByteArray(), defaultQoS, false)
    }
}

internal suspend fun wearableCommLogic(
    comm: WearableComm,
    behaviourRef: BehaviourRef<DistanceFromSource>,
) = coroutineScope {
    val j1 = launch {
        comm.receive().collect {
            behaviourRef.sendToComponent(it)
        }
    }
    val j2 = launch {
        behaviourRef.receiveFromComponent().collect {
            comm.send(it)
        }
    }

    j1.join()
    j2.join()
}
