package wtf.log.xmas2017.days.day10

import wtf.log.xmas2017.util.setAll
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import kotlin.experimental.xor

class KnotDigest : MessageDigest("KNOT") {

    class RepeatingByteArray(private val size: Int = 256) {

        private val values = ByteArray(size) { it.toByte() }

        operator fun get(index: Int): Byte {
            return values[index % size]
        }

        operator fun set(index: Int, value: Byte) {
            values[index % size] = value
        }

        fun reverse(startIndex: Int, endIndex: Int) {
            val lastIndex = endIndex - 1
            val length = endIndex - startIndex
            for (i in 0 until length / 2) {
                val leftIndex = startIndex + i
                val rightIndex = lastIndex - i
                val temp = get(leftIndex)
                set(leftIndex, get(rightIndex))
                set(rightIndex, temp)
            }
        }

        fun clear() {
            values.setAll { it.toByte() }
        }

        fun blocks(size: Int = 16): Sequence<ByteArray> {
            return values.asSequence().chunked(size) { it.toByteArray() }
        }

        override fun toString(): String = values.contentToString()

    }

    private val hash = RepeatingByteArray()
    private val data = ByteArrayOutputStream()

    override fun engineGetDigestLength(): Int = 16

    override fun engineReset() {
        hash.clear()
        data.reset()
    }

    override fun engineDigest(): ByteArray {
        val data = data.apply {
            write(PADDING)
        }
        val input = data.toByteArray()
        val hash = hash
        var position = 0
        var skip = 0
        repeat(times = 64) {
            for (value in input) {
                hash.reverse(position, position + value)
                position += value + skip
                skip++
            }
        }
        data.reset()
        hash.blocks(size = 16).map { it.reduce(Byte::xor) }.forEach { byte ->
            data.write(byte.toInt())
        }
        val result = data.toByteArray()
        reset()
        return result
    }

    override fun engineUpdate(input: Byte) {
        data.write(input.toInt())
    }

    override fun engineUpdate(input: ByteArray, offset: Int, len: Int) {
        data.write(input, offset, len)
    }

    companion object {

        private val PADDING = byteArrayOf(17, 31, 73, 47, 23)

    }

}
