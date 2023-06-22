package it.nicolasfarabegoli.prin.commonwears

import it.nicolasfarabegoli.pulverization.runtime.dsl.model.ReconfigurationEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onEach

class LowBatteryEvent : ReconfigurationEvent<Double>() {
    override val events: Flow<Double> = generateSequence(100.0) { it - 10.0  }.asFlow().onEach { delay(500) }
    override val predicate: (Double) -> Boolean = { it < 20.0 }
}