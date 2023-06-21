package it.nicolasfarabegoli.prin.commonwears

import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import com.welie.blessed.BluetoothPeripheralManager
import com.welie.blessed.BluetoothPeripheralManagerCallback
import it.nicolasfarabegoli.prin.commonwears.wearable.SignalStrengthValue
import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.Sensor
import it.nicolasfarabegoli.pulverization.core.SensorsContainer
import it.nicolasfarabegoli.pulverization.runtime.componentsref.BehaviourRef
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.koin.core.component.inject
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds
import android.content.Context as AndroidContext
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.os.ParcelUuid
import android.util.Log
import com.welie.blessed.BluetoothCentralManager

/**
 * Sensors that perceive the target using BLE.
 */
class WearableSensor(private val context: AndroidContext) : Sensor<SignalStrengthValue> {
    private val bleCallback = object : BluetoothPeripheralManagerCallback() { }
    private val advertisingSettings = AdvertiseSettings.Builder()
        .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
        .setConnectable(true)
        .setTimeout(0)
        .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH).build()
    private val advData = AdvertiseData.Builder()
        .setIncludeTxPowerLevel(true)
        .addServiceUuid(ParcelUuid(UUID.randomUUID()))
        .build()
    private val advResponse = AdvertiseData.Builder()
        .setIncludeDeviceName(true).build()
    private val btCentralManager by lazy { BluetoothCentralManager(context) }

    override suspend fun sense(): SignalStrengthValue = SignalStrengthValue(rssi)

    private var rssi = 0

    override suspend fun initialize(): Unit = coroutineScope {
        val bleManager = context.getSystemService(AndroidContext.BLUETOOTH_SERVICE) as BluetoothManager
        val phManager = BluetoothPeripheralManager(context, bleManager, bleCallback)
        val emptyGattService = BluetoothGattService(
            UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb"),
            SERVICE_TYPE_PRIMARY
        )
        phManager.add(emptyGattService)
        phManager.startAdvertising(advertisingSettings, advData, advResponse)

        Log.i("WearableSensor", "Setup completed")

        startScanning()
    }

    private fun startScanning() {
        btCentralManager.scanForPeripherals(
            { bluetoothPeripheral, scanResult ->
                if (bluetoothPeripheral.name == "Findme") {
                    rssi = scanResult.rssi
                    Log.i("Sensor", "RPI ${bluetoothPeripheral.name} ${scanResult.rssi}")
                }
            },
            {
                Log.e("Sensor", "Fail to scan: $it")
            }
        )
    }
}

/**
 * Sensors container.
 */
class WearableSensorsContainer(private val aContext: AndroidContext) : SensorsContainer() {
    override val context: Context by inject()

    override suspend fun initialize() {
        this += WearableSensor(aContext).apply { initialize() }
    }
}

/**
 * Sensors logic.
 */
internal suspend fun wearableSensorsLogic(
    sensors: SensorsContainer,
    behaviourRef: BehaviourRef<SignalStrengthValue>,
) = coroutineScope {
    sensors.get<WearableSensor> {
        while (true) {
            behaviourRef.sendToComponent(sense())
            delay(1000.milliseconds)
        }
    }
}
