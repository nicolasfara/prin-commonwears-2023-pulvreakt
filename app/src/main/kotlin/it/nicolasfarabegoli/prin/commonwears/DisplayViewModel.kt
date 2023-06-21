package it.nicolasfarabegoli.prin.commonwears

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import it.nicolasfarabegoli.prin.commonwears.wearable.WearableDisplayInfo

class DisplayViewModel : ViewModel() {
    var neighbourDistances by mutableStateOf(mapOf<String, Double>())
        private set

    var currentDistance by mutableStateOf(0.0)
        private set

    fun update(displayInfo: WearableDisplayInfo) {
        val (distances, myDistance) = displayInfo
        neighbourDistances = distances
        currentDistance = myDistance
    }
}