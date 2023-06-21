package it.nicolasfarabegoli.prin.commonwears.wearable

import it.nicolasfarabegoli.prin.commonwears.wearable.utils.Filter
import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.Behaviour
import it.nicolasfarabegoli.pulverization.core.BehaviourOutput
import it.nicolasfarabegoli.pulverization.runtime.componentsref.ActuatorsRef
import it.nicolasfarabegoli.pulverization.runtime.componentsref.CommunicationRef
import it.nicolasfarabegoli.pulverization.runtime.componentsref.SensorsRef
import it.nicolasfarabegoli.pulverization.runtime.componentsref.StateRef
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import kotlin.math.pow

/**
 * Behaviour f the wearable.
 */
class WearableBehaviour : Behaviour<Unit, DistanceFromSource, SignalStrengthValue, WearableDisplayInfo, Unit> {
    override val context: Context by inject()
    private val filter = Filter<Int>(WINDOW)

    companion object {
        private const val RSSI_ONE_METER = -64
        private const val SIGNAL_PATH_LOSS = 10 * 2.5
        private const val WINDOW = 5
    }

    override fun invoke(
        state: Unit,
        export: List<DistanceFromSource>,
        sensedValues: SignalStrengthValue,
    ): BehaviourOutput<Unit, DistanceFromSource, WearableDisplayInfo, Unit> {
        filter.register(sensedValues.rssi)
        val filteredRssi = filter.get().toInt()
        val distance = 10.0.pow((RSSI_ONE_METER - filteredRssi) / SIGNAL_PATH_LOSS)
        val neighbourDistance = export.associate { it.deviceId to it.distance }
        val displayInfo = WearableDisplayInfo(neighbourDistance, distance)
        return BehaviourOutput(Unit, DistanceFromSource(context.deviceID, distance), displayInfo, Unit)
    }
}

/**
 * Behaviour logic.
 */
suspend fun wearableBehaviourLogic(
    behaviour: Behaviour<Unit, DistanceFromSource, SignalStrengthValue, WearableDisplayInfo, Unit>,
    @Suppress("UNUSED_PARAMETER") stateRef: StateRef<Unit>,
    commRef: CommunicationRef<DistanceFromSource>,
    sensorsRef: SensorsRef<SignalStrengthValue>,
    actuatorsRef: ActuatorsRef<WearableDisplayInfo>,
) = coroutineScope {
    val messages = mutableListOf<DistanceFromSource>()
    val jComm = launch {
        commRef.receiveFromComponent().collect {
            messages.removeIf { elem -> elem.deviceId == it.deviceId }
            messages.add(it)
        }
    }

    sensorsRef.receiveFromComponent().collect { sensorValue ->
        val (_, newMessage, action) = behaviour(Unit, messages, sensorValue)
        commRef.sendToComponent(newMessage)
        actuatorsRef.sendToComponent(action)
    }

    jComm.join()
}
