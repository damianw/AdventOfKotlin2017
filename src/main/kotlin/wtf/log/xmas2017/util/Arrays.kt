package wtf.log.xmas2017.util

@Suppress("NOTHING_TO_INLINE")
inline fun CharArray.copyTo(
        destination: CharArray,
        sourcePosition: Int = 0,
        destinationPosition: Int = 0,
        length: Int = size
) {
    System.arraycopy(this, sourcePosition, destination, destinationPosition, length)
}

inline fun ByteArray.setAll(generator: (Int) -> Byte) {
    for (i in indices) {
        set(i, generator(i))
    }
}
