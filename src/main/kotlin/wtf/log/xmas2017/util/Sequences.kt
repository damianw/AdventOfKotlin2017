package wtf.log.xmas2017.util

import kotlin.coroutines.experimental.buildSequence

fun <T, R> Sequence<T>.scan(initial: R, operation: (R, T) -> R): Sequence<R> = buildSequence {
    var accumulator = initial
    for (element in this@scan) {
        yield(accumulator)
        accumulator = operation(accumulator, element)
    }
    yield(accumulator)
}

inline fun <T, C> Sequence<T>.collectTo(collector: C, operation: C.(T) -> Unit): C {
    forEach { collector.operation(it) }
    return collector
}
