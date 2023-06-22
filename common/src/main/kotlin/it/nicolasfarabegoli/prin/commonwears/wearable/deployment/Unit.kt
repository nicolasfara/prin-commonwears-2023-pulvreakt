package it.nicolasfarabegoli.prin.commonwears.wearable.deployment

import it.nicolasfarabegoli.pulverization.runtime.PulverizationRuntime

suspend fun main(args: Array<String>) {
    require(args.size == 1)
    val runtimeConfig = runtimeConfig(config)
    val platform = PulverizationRuntime(args[0], "alice", runtimeConfig)
    platform.start()
}
