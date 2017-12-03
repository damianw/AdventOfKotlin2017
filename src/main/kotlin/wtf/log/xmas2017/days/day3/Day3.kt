package wtf.log.xmas2017.days.day3

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import kotlin.math.abs
import kotlin.math.sqrt

object Day3 : Solver<Int, Int> {

    private fun part1(input: Int): Int {
        val previousRoot = sqrt(input.toDouble()).toInt().let { sqrt ->
            if (sqrt % 2 == 0) sqrt - 1 else sqrt
        }
        val currentRoot = previousRoot + 2
        val perSide = currentRoot - 1
        val ringStart = previousRoot * previousRoot + 1
        val diff = input - ringStart
        val adjustedDistance = diff + 1
        val onSide = adjustedDistance % perSide
        val distanceToCenter = currentRoot / 2
        val distanceToCenterOfSide = abs(onSide - distanceToCenter)
        return distanceToCenter + distanceToCenterOfSide
    }

    enum class Direction {
        LEFT,
        UP,
        RIGHT,
        DOWN
    }

    private operator fun <R, C, V> Table<R, C, V>.set(rowKey: R, columnKey: C, value: V) {
        put(rowKey, columnKey, value)
    }

    private fun Table<Int, Int, Int>.sumNeighbors(rowKey: Int, columnKey: Int): Int = sequenceOf(
            get(rowKey - 1, columnKey - 1),
            get(rowKey - 1, columnKey),
            get(rowKey - 1, columnKey + 1),
            get(rowKey, columnKey + 1),
            get(rowKey + 1, columnKey + 1),
            get(rowKey + 1, columnKey),
            get(rowKey + 1, columnKey - 1),
            get(rowKey, columnKey - 1)
    ).filterNotNull().sum()

    private fun part2(input: Int): Int {
        val table = HashBasedTable.create<Int, Int, Int>()
        var row = 0
        var column = 0
        var direction = Direction.RIGHT
        var currentValue = 1

        table[0, 0] = currentValue
        while (currentValue <= input) {
            when (direction) {
                Direction.RIGHT -> {
                    column += 1
                    table[row, column] = table.sumNeighbors(row, column)
                    if (table[row - 1, column] == null) {
                        direction = Direction.UP
                    }
                }
                Direction.UP -> {
                    row -= 1
                    table[row, column] = table.sumNeighbors(row, column)
                    if (table[row, column - 1] == null) {
                        direction = Direction.LEFT
                    }
                }
                Direction.LEFT -> {
                    column -= 1
                    table[row, column] = table.sumNeighbors(row, column)
                    if (table[row + 1, column] == null) {
                        direction = Direction.DOWN
                    }
                }
                Direction.DOWN -> {
                    row += 1
                    table[row, column] = table.sumNeighbors(row, column)
                    if (table[row, column + 1] == null) {
                        direction = Direction.RIGHT
                    }
                }
            }
            currentValue = table[row, column]
        }
        return currentValue
    }

    private const val INPUT = 361527

    override fun solve() = Solution(
            part1 = part1(INPUT),
            part2 = part2(INPUT)
    )

}
