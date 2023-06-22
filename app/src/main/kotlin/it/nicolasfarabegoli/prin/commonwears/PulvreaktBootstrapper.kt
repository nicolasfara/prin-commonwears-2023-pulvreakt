package it.nicolasfarabegoli.prin.commonwears

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.UUID

class PulvreaktBootstrapper {
    private val deviceId: String = UUID.randomUUID().toString()
    private val topic = "bootstrap/$deviceId"
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
        }
    }

    suspend fun finalize() = withContext(Dispatchers.IO) {
        mqttClient.publish(topic, "shutdown($deviceId)".toByteArray(), QOS, false)
        mqttClient.disconnect()
    }

    suspend fun bootstrap(): String = withContext(Dispatchers.IO) {
        mqttClient.publish(topic, deviceId.toByteArray(), QOS, false)
        return@withContext deviceId
    }

    companion object {
        private const val HOSTNAME = "192.168.8.1"
        private const val PORT = 1883
        private const val QOS = 2
    }
}