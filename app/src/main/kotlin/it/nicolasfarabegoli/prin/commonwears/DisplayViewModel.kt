package it.nicolasfarabegoli.prin.commonwears

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableDisplayInfo

/**
 * View model class holding information for the UI.
 */
class DisplayViewModel : ViewModel() {
    /**
     * Distances of the neighbours.
     */
    var neighbourDistances by mutableStateOf(mapOf<String, Double>())
        private set

    /**
     * The current distance of the device.
     */
    var currentDistance by mutableStateOf(0.0)
        private set

    /**
     * Show if the behaviour is offloaded.
     */
    var behaviourOffloaded by mutableStateOf(false)
        private set

    /**
     * Update the UI state.
     */
    fun update(displayInfo: WearableDisplayInfo) {
        val (distances, myDistance) = displayInfo
        neighbourDistances = distances
        currentDistance = myDistance
    }

    /**
     * Set if the behaviour is offloaded or not.
     */
    fun behaviourOffloaded(isOffloaded: Boolean) {
        behaviourOffloaded = isOffloaded
    }
}
