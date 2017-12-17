@file:Suppress("NOTHING_TO_INLINE")

package wtf.log.xmas2017.util

inline fun Int.bitCount(): Int = java.lang.Integer.bitCount(this)

inline fun Byte.toUnsignedInt(): Int = java.lang.Byte.toUnsignedInt(this)

inline fun Int.Companion.fromBytes(a: Byte, b: Byte, c: Byte, d: Byte): Int {
    return d.toUnsignedInt() or
            (c.toUnsignedInt() shl 8) or
            (b.toUnsignedInt() shl 16) or
            (a.toUnsignedInt() shl 24)
}
