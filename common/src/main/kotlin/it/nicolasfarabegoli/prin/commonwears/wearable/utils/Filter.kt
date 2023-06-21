package it.nicolasfarabegoli.prin.commonwears.wearable.utils

/**
 * Average moving filter with a specified [window] size.
 */
class Filter<in T>(private val window: Int) where T : Number, T : Comparable<T> {

    private val queue = ArrayDeque<T>(window)

    /**
     * Add new [element] to the filter.
     */
    fun register(element: T) {
        if (queue.size > window) {
            queue.removeFirst()
        }
        queue.addLast(element)
    }

    /**
     * Return the average value computed by the filter.
     */
    fun get(): Double = queue.sumOf { it.toDouble() } / window
}
