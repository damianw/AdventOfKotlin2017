package wtf.log.xmas2017.days.Day10

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver

object Day10 : Solver<Int, String> {

    private class RepeatingIntArray(val size: Int = 256) {

        private val values = IntArray(size) { it }

        operator fun get(index: Int): Int {
            return values[index % size]
        }

        operator fun set(index: Int, value: Int) {
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

        fun blocks(size: Int = 16): Sequence<IntArray> {
            return values.asSequence().chunked(size) { it.toIntArray() }
        }

        override fun toString(): String = values.contentToString()

    }

    private val INPUT = "187,254,0,81,169,219,1,190,19,102,255,56,46,32,2,216"

    private fun part1(): Int {
        val array = RepeatingIntArray()
        val lengths = INPUT.split(',').map { it.toInt() }.toIntArray()
        var position = 0
        lengths.forEachIndexed { skip, length ->
            array.reverse(position, position + length)
            position += length + skip
        }
        return array[0] * array[1]
    }

    private fun part2(): String {
        val array = RepeatingIntArray()
        val lengths = (INPUT.map { it.toInt() } + listOf(17, 31, 73, 47, 23)).toIntArray()
        var position = 0
        var skip = 0
        repeat(64) {
            for (length in lengths) {
                array.reverse(position, position + length)
                position += length + skip
                skip++
            }
        }
        return array.blocks()
                .map { it.reduce(Int::xor) }
                .joinToString("") { "%02X".format(it) }
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
