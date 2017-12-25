package wtf.log.xmas2017.days.day22

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import java.io.BufferedReader
import kotlin.collections.set

object Day22 : Solver<Int, Int> {

    private data class Point(val row: Int, val col: Int) {
        fun inDirection(direction: Direction) = Point(row = row + direction.dy, col = col + direction.dx)
    }

    private enum class Direction(val dx: Int = 0, val dy: Int = 0) {
        LEFT(dx = -1),
        UP(dy = -1),
        RIGHT(dx = 1),
        DOWN(dy = 1);

        fun offset(amount: Int): Direction = values[(ordinal + amount) % values.size]

        val next: Direction get() = offset(1)
        val previous: Direction get() = offset(3)

        companion object {
            private val values = values()
        }
    }

    private class Network {

        private val nodes = mutableMapOf<Point, NodeState>()

        val rows: Sequence<Int> get() = nodes.keys.asSequence().map { it.row }
        val cols: Sequence<Int> get() = nodes.keys.asSequence().map { it.col }
        val rowRange: IntRange get() = rows.min()!!..rows.max()!!
        val colRange: IntRange get() = cols.min()!!..cols.max()!!

        operator fun get(point: Point): NodeState = nodes[point] ?: NodeState.CLEANED

        operator fun set(point: Point, state: NodeState) {
            nodes[point] = state
        }

        override fun toString() = buildString {
            for (row in rowRange) {
                for (col in colRange) {
                    append(get(Point(row, col)).symbol)
                }
                append('\n')
            }
        }

        enum class NodeState(val symbol: Char, val directionOffset: Int) {
            CLEANED(symbol = '.', directionOffset = 3),
            WEAKENED(symbol = 'W', directionOffset = 0),
            INFECTED(symbol = '#', directionOffset = 1),
            FLAGGED(symbol = 'F', directionOffset = 2);

            val next: NodeState get() = values[(ordinal + 1) % values.size]

            companion object {

                private val values = values()

                fun fromSymbol(symbol: Char): NodeState = values.first { it.symbol == symbol }

            }
        }

        companion object {

            fun parse(input: BufferedReader) = Network().apply {
                input.lineSequence().forEachIndexed { row, line ->
                    line.forEachIndexed { col, char ->
                        set(Point(row, col), NodeState.fromSymbol(char))
                    }
                }
            }

        }

    }

    private abstract class Carrier(val network: Network) {

        var location: Point = Point(row = network.rowRange.mean, col = network.colRange.mean)
            protected set
        var direction: Direction = Direction.UP
            protected set
        var infectionsCaused: Int = 0
            protected set

        abstract fun burst()

    }

    private class PrimitiveCarrier(network: Network): Carrier(network) {

        override fun burst() {
            if (network[location] == Network.NodeState.INFECTED) {
                direction = direction.next
                network[location] = Network.NodeState.CLEANED
            } else {
                direction = direction.previous
                network[location] = Network.NodeState.INFECTED
                infectionsCaused++
            }
            location = location.inDirection(direction)
        }

    }

    private class EvolvedCarrier(network: Network): Carrier(network) {

        override fun burst() {
            val currentState = network[location]
            val nextState = currentState.next
            direction = direction.offset(currentState.directionOffset)
            network[location] = currentState.next
            location = location.inDirection(direction)
            if (nextState == Network.NodeState.INFECTED) {
                infectionsCaused++
            }
        }

    }

    private fun readInput(): Network = openResource("Day22.txt").use((Network)::parse)

    private val IntRange.mean: Int get() = start + ((endInclusive - start) / 2)

    private inline fun solve(iterations: Int, create: (Network) -> Carrier): Int {
        return create(readInput()).apply { repeat(iterations) { burst() } }.infectionsCaused
    }

    private fun part1(): Int = solve(10000, ::PrimitiveCarrier)

    private fun part2(): Int = solve(10000000, ::EvolvedCarrier)

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
