package it.nicolasfarabegoli.prin.commonwears.wearable.deployment

import it.nicolasfarabegoli.prin.commonwears.wearable.PulvreaktBootstrapper
import it.nicolasfarabegoli.pulverization.runtime.PulverizationRuntime
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Entrypoint.
 */
suspend fun main() = runBlocking {
    val spawnedMap = mutableMapOf<String, Job>()

    val bootstrapper = PulvreaktBootstrapper().apply { initialize() }
    bootstrapper.bootstrap().collect {
        when (it) {
            "shutdown" -> TODO()
            else -> {
                val job = launch { spawnDeploymentUnit(it) }
                spawnedMap += it to job
            }
        }
    }
}

private suspend fun spawnDeploymentUnit(deviceId: String) {
    val runtimeConfig = runtimeConfig(config)
    val platform = PulverizationRuntime(deviceId, "alice", runtimeConfig)
    platform.start()
}
