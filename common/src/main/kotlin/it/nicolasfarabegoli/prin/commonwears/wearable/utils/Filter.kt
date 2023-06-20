package it.nicolasfarabegoli.prin.commonwears.wearable.utils

class Filter<in T>(private val window: Int) where T : Number, T : Comparable<T> {

    private val queue = ArrayDeque<T>(window)

    fun register(element: T) {
        if (queue.size > window) {
            queue.removeFirst()
        }
        queue.addLast(element)
    }

    fun get(): Double = queue.sumOf { it.toDouble() } / window
}
