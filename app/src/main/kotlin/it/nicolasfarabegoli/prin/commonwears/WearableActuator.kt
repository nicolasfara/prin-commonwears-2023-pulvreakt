package it.nicolasfarabegoli.prin.commonwears

import android.util.Log
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableDisplayInfo
import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.Actuator
import it.nicolasfarabegoli.pulverization.core.ActuatorsContainer
import it.nicolasfarabegoli.pulverization.runtime.componentsref.BehaviourRef
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.inject

/**
 * Actuator that show on the screen information about neighbours.
 */
class WearableActuator(private val display: DisplayViewModel) : Actuator<WearableDisplayInfo> {
    override suspend fun actuate(payload: WearableDisplayInfo) {
        Log.i("WearableActuator", "Actuate: $payload")
        display.update(payload)
    }
}

/**
 * Actuators container.
 */
class WearableActuatorsContainer(private val display: DisplayViewModel) : ActuatorsContainer() {
    override val context: Context by inject()

    override suspend fun initialize() {
        this += WearableActuator(display)
    }
}

/**
 * Wearable actuators logic.
 */
suspend fun wearableActuatorsLogic(
    actuators: ActuatorsContainer,
    behaviourRef: BehaviourRef<WearableDisplayInfo>,
) = coroutineScope {
    actuators.get<WearableActuator> {
        behaviourRef.receiveFromComponent().collect {
            actuate(it)
        }
    }
}
