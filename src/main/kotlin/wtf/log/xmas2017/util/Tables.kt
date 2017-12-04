package wtf.log.xmas2017.util

import com.google.common.collect.Table

/**
 * Kotlin operator for putting a value in a [Table]
 */
@Suppress("NOTHING_TO_INLINE")
inline operator fun <R, C, V> Table<R, C, V>.set(rowKey: R, columnKey: C, value: V) {
  put(rowKey, columnKey, value)
}
