package it.nicolasfarabegoli.prin.commonwears

import androidx.lifecycle.viewModelScope
import it.nicolasfarabegoli.pulverization.core.Initializable
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.FailOnReconfiguration
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.ReconfigurationEvent
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.ReconfigurationSuccess
import it.nicolasfarabegoli.pulverization.runtime.dsl.model.SkipCheck
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LowBatteryEvent(private val display: DisplayViewModel) : ReconfigurationEvent<Double>(), Initializable {
    override suspend fun initialize() {
        display.viewModelScope.launch {
            results.collect {
                when (it) {
                    is ReconfigurationSuccess -> display.behaviourOffloaded(true)
                    else -> Unit
                }
            }
        }
    }
    override val events: Flow<Double> = generateSequence(100.0) { it - 5.0  }.asFlow().onEach { delay(500) }
    override val predicate: (Double) -> Boolean = { it < 20.0 }
}