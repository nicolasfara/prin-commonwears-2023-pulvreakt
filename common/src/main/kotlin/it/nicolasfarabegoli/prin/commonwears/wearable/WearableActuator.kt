package it.nicolasfarabegoli.prin.commonwears.wearable

import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.Actuator
import it.nicolasfarabegoli.pulverization.core.ActuatorsContainer
import it.nicolasfarabegoli.pulverization.runtime.componentsref.BehaviourRef
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.inject

typealias WearableDistances = Map<String, Double>

class WearableActuator : Actuator<WearableDistances> {
    override suspend fun actuate(payload: WearableDistances) {
        TODO("Not yet implemented")
    }
}

class WearableActuatorsContainer : ActuatorsContainer() {
    override val context: Context by inject()

    override suspend fun initialize() {
        this += WearableActuator()
    }
}

internal suspend fun wearableActuatorsLogic(
    actuators: WearableActuatorsContainer,
    behaviourRef: BehaviourRef<WearableDistances>,
) = coroutineScope {
    actuators.get<WearableActuator> {
        behaviourRef.receiveFromComponent().collect {
            actuate(it)
        }
    }
}
