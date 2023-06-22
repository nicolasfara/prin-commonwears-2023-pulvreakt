package it.nicolasfarabegoli.prin.commonwears.wearable

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

/**
 * TODO
 */
class PulvreaktBootstrapper {
    private val mqttClient = MqttClient("tcp://$HOSTNAME:$PORT", MqttClient.generateClientId(), MemoryPersistence())
    private val logger = Logger.withTag("MqttCommunicator")

    suspend fun initialize() {
        logger.i { "Setup bootstrapper" }
        val mqttConnectionOption = MqttConnectOptions().apply {
            userName = ""
            password = "".toCharArray()
        }
        withContext(Dispatchers.IO) {
            mqttClient.connect(mqttConnectionOption)
            mqttClient.subscribe(TOPIC)
        }
    }

    /**
     * TODO
     */
    suspend fun finalize() = withContext(Dispatchers.IO) {
        mqttClient.disconnect()
    }

    /**
     * TODO
     */
    suspend fun bootstrap(): Flow<String> = callbackFlow {
        val callback = object : MqttCallback {
            override fun connectionLost(cause: Throwable?) {
                cancel(CancellationException("Connection lost", cause))
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                requireNotNull(message) { "Message cannot be empty" }
                trySend(message.payload.decodeToString())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) = Unit
        }
        mqttClient.setCallback(callback)
        awaitClose { mqttClient.unsubscribe(TOPIC) }
    }

    companion object {
        private const val HOSTNAME = "192.168.8.1"
        private const val PORT = 1883
        private const val TOPIC = "bootstrap/#"
    }
}
