package wtf.log.xmas2017.days.day14

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.days.day10.KnotDigest
import wtf.log.xmas2017.util.bitCount
import wtf.log.xmas2017.util.fromBytes
import java.util.*

object Day14 : Solver<Int, Int> {

    data class Point(val x: Int, val y: Int)

    class CompactBooleanGrid {

        private val matrix = IntArray(size = SIZE * 4)

        operator fun set(row: Int, values: ByteArray) {
            require(values.size == SIZE / 8)
            val offset = row * 4
            for (i in 0..values.lastIndex / 4) {
                val index = i * 4
                matrix[offset + i] = Int.fromBytes(
                        values[index],
                        values[index + 1],
                        values[index + 2],
                        values[index + 3]
                )
            }
        }

        operator fun set(x: Int, y: Int, value: Boolean) {
            require(x in 0 until SIZE && y in 0 until SIZE)
            val bit = y * SIZE + x
            val index = bit / 32
            val offset = 31 - (bit % 32)
            val current = matrix[index]
            val mask = (0x1 shl offset)
            val enabled = current or mask
            matrix[index] = if (value) enabled else enabled xor mask
        }

        operator fun get(x: Int, y: Int): Boolean {
            if (x !in 0 until SIZE || y !in 0 until SIZE) return false
            val bit = y * 128 + x
            val index = bit / 32
            val offset = 31 - (bit % 32)
            val int = matrix[index]
            return (int ushr offset) and 0x01 == 1
        }

        fun populationCount(): Int = matrix.sumBy { it.bitCount() }

        override fun toString() = buildString {
            append('┌')
            repeat(SIZE) {
                append('─')
            }
            append("┐\n")
            repeat(SIZE) { y ->
                append('│')
                repeat(SIZE) { x ->
                    append(if (get(x, y)) '#' else '.')
                }
                append("│\n")
            }
            append('└')
            repeat(SIZE) {
                append('─')
            }
            append('┘')
        }

        companion object {

            const val SIZE = 128

        }

    }

    private const val INPUT = "hwlqcszp"

    private val grid = CompactBooleanGrid().apply {
        val digest = KnotDigest()
        val data = INPUT.toByteArray()
        repeat(CompactBooleanGrid.SIZE) { row ->
            digest.update(data)
            digest.update('-'.toByte())
            digest.update(row.toString().toByteArray())
            set(row, digest.digest())
        }
    }

    private fun part1(): Int = grid.populationCount()

    private fun CompactBooleanGrid.consumeRegion(x: Int, y: Int): Boolean {
        if (!get(x, y)) return false
        val queue = ArrayDeque<Point>().apply { add(Point(x, y)) }
        while (!queue.isEmpty()) {
            val point = queue.poll()
            set(point.x, point.y, false)
            if (get(point.x, point.y + 1)) {
                queue.offer(Point(point.x, point.y + 1))
            }
            if (get(point.x, point.y - 1)) {
                queue.offer(Point(point.x, point.y - 1))
            }
            if (get(point.x + 1, point.y)) {
                queue.offer(Point(point.x + 1, point.y))
            }
            if (get(point.x - 1, point.y)) {
                queue.offer(Point(point.x - 1, point.y))
            }
        }
        return true
    }

    private fun part2(): Int {
        var count = 0
        repeat(CompactBooleanGrid.SIZE) { y ->
            repeat(CompactBooleanGrid.SIZE) { x ->
                if (grid.consumeRegion(x, y)) {
                    count++
                }
            }
        }
        return count
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
