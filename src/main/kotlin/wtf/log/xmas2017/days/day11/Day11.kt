package wtf.log.xmas2017.days.day11

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import wtf.log.xmas2017.util.scan

object Day11 : Solver<Int, Int> {

    private enum class HexDirection(val symbol: String) {
        NORTH("n") {
            override fun plus(cell: HexCell) = HexCell(
                    x = cell.x,
                    y = cell.y + 1
            )
        },
        NORTH_EAST("ne"){
            override fun plus(cell: HexCell) = HexCell(
                    x = cell.x + 1,
                    y = cell.y + if (cell.x % 2 == 0) 0 else 1
            )
        },
        SOUTH_EAST("se") {
            override fun plus(cell: HexCell) = HexCell(
                    x = cell.x + 1,
                    y = cell.y - if (cell.x % 2 == 0) 1 else 0
            )
        },
        SOUTH("s") {
            override fun plus(cell: HexCell) = HexCell(
                    x = cell.x,
                    y = cell.y - 1
            )
        },
        SOUTH_WEST("sw") {
            override fun plus(cell: HexCell) = HexCell(
                    x = cell.x - 1,
                    y = cell.y - if (cell.x % 2 == 0) 1 else 0
            )
        },
        NORTH_WEST("nw") {
            override fun plus(cell: HexCell) = HexCell(
                    x = cell.x - 1,
                    y = cell.y + if (cell.x % 2 == 0) 0 else 1
            )
        };

        abstract operator fun plus(cell: HexCell): HexCell

        companion object {
            fun fromSymbol(symbol: String) = values().first { it.symbol == symbol }
        }
    }

    private data class HexCell(val x: Int, val y: Int) {

        operator fun plus(direction: HexDirection): HexCell = direction + this

        fun pathToOrigin(): Sequence<HexCell> = generateSequence(this) { current ->
            current + when {
                current.x < 0 -> if (current.y < 0) HexDirection.NORTH_EAST else HexDirection.SOUTH_EAST
                current.x > 0 -> if (current.y < 0) HexDirection.NORTH_WEST else HexDirection.SOUTH_WEST
                else -> when {
                    current.y < 0 -> HexDirection.NORTH
                    current.y > 0 -> HexDirection.SOUTH
                    else -> return@generateSequence null
                }
            }
        }.drop(1)

        fun distanceToOrigin(): Int = pathToOrigin().count()

        companion object {

            val ORIGIN = HexCell(x = 0, y = 0)

        }

    }

    private fun readInput(): Sequence<HexDirection> {
        return openResource("Day11.txt")
                .use { it.readText().trim() }
                .split(',')
                .asSequence()
                .map((HexDirection)::fromSymbol)
    }

    private fun part1(): Int {
        return readInput()
                .fold(HexCell.ORIGIN) { cell, direction -> cell + direction }
                .distanceToOrigin()
    }

    private fun part2(): Int {
        return readInput()
                .scan(HexCell.ORIGIN) { cell, direction -> cell + direction }
                .map { it.distanceToOrigin() }
                .max()!!
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
