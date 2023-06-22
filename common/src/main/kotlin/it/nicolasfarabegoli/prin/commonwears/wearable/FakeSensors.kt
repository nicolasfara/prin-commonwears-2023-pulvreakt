package it.nicolasfarabegoli.prin.commonwears.wearable

import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.SensorsContainer
import org.koin.core.component.inject

/**
 * TODO.
 */
class FakeSensors : SensorsContainer() {
    override val context: Context by inject()
}
