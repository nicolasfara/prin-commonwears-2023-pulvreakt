package it.nicolasfarabegoli.prin.commonwears.wearable

import it.nicolasfarabegoli.pulverization.component.Context
import it.nicolasfarabegoli.pulverization.core.ActuatorsContainer
import org.koin.core.component.inject

/**
 * TODO.
 */
class FakeActuators : ActuatorsContainer() {
    override val context: Context by inject()
}
