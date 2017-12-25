package wtf.log.xmas2017.days.day21

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import wtf.log.xmas2017.util.copyTo
import java.util.*

object Day21 : Solver<Int, Int> {

    class Grid<T : Any?>(val size: Int) : Iterable<T> {

        private val values = arrayOfNulls<Any>(size * size)

        private fun requireInRange(row: Int, col: Int) {
            require(row in 0 until size) { "Row not in range: $row" }
            require(col in 0 until size) { "Column not in range: $col" }
        }

        @Suppress("UNCHECKED_CAST")
        operator fun get(row: Int, col: Int): T {
            requireInRange(row, col)
            return values[row * size + col] as T
        }

        operator fun set(row: Int, col: Int, value: T) {
            requireInRange(row, col)
            values[row * size + col] = value
        }

        @Suppress("UNCHECKED_CAST")
        fun map(transform: (T) -> T): Grid<T> = ofValues(size, values.asSequence().map { transform(it as T) })

        @Suppress("UNCHECKED_CAST")
        override fun iterator(): Iterator<T> = values.iterator() as Iterator<T>

        override fun equals(other: Any?): Boolean {
            return other is Grid<*> && values.contentEquals(other.values)
        }

        override fun hashCode(): Int {
            var result = size
            result = 31 * result + Arrays.hashCode(values)
            return result
        }

        override fun toString() = buildString {
            append('┌')
            repeat(size) {
                append('─')
            }
            append("┐\n")
            repeat(size) { row ->
                append('│')
                repeat(size) { col ->
                    append(get(row, col))
                }
                append("│\n")
            }
            append('└')
            repeat(size) {
                append('─')
            }
            append('┘')
        }

        companion object {

            fun <T : Any?> ofValues(size: Int, values: Sequence<T>) = Grid<T>(size).also { grid ->
                values.toList().toTypedArray<Any?>().copyTo(grid.values)
            }

            fun parseChars(input: String): Grid<Char> {
                val parts = input.split('/')
                return ofValues(size = parts.first().length, values = parts.joinToString("").asSequence())
            }

        }

    }

    data class Rule(val pattern: Grid<Char>, val output: Grid<Char>) {

        companion object {

            fun parse(input: String): Rule {
                val (pattern, output) = input.split(" => ")
                return Rule(Grid.parseChars(pattern), Grid.parseChars(output))
            }

        }

    }

    private fun <T> Grid<T>.divide(): Grid<Grid<T>> {
        if (size % 2 == 0) {
            val newValues = (0 until size step 2)
                    .asSequence()
                    .flatMap { row ->
                        (0 until size step 2)
                                .asSequence()
                                .map { col ->
                                    Grid.ofValues(size = 2, values = sequenceOf(
                                            get(row, col),
                                            get(row, col + 1),
                                            get(row + 1, col),
                                            get(row + 1,  col + 1)
                                    ))
                                }
                    }
            return Grid.ofValues(size = size / 2, values = newValues)
        }
        assert(size % 3 == 0)
        val newValues = (0 until size step 3)
                .asSequence()
                .flatMap { row ->
                    (0 until size step 3)
                            .asSequence()
                            .map { col ->
                                Grid.ofValues(size = 3, values = sequenceOf(
                                        get(row, col),
                                        get(row, col + 1),
                                        get(row, col + 2),
                                        get(row + 1, col),
                                        get(row + 1, col + 1),
                                        get(row + 1, col + 2),
                                        get(row + 2, col),
                                        get(row + 2,col + 1),
                                        get(row + 2, col + 2)
                                ))
                            }
                }
        return Grid.ofValues(size = size / 3, values = newValues)
    }

    private fun <T : Any> Grid<T>.reflections(): Sequence<Grid<T>> {
        val dimensions = 0 until size
        val ranges = sequenceOf(dimensions, dimensions.reversed())
        return ranges.map { rowRange ->
            ranges.map { colRange ->
                Grid.ofValues(size, rowRange.flatMap { row ->
                    colRange.map { col ->
                        get(row, col)
                    }
                }.asSequence())
            }
        }.flatten()
    }

    private fun <T : Any> Grid<T>.rotate(): Grid<T> {
        val result = Grid.ofValues(size, asSequence())
        for (row in 0 until size / 2) {
            for (col in row until size - row - 1) {
                val temp = get(row, col)
                result[row, col] = get(col, size - 1 - row)
                result[col, size - 1 - row] = result[size - 1 - row, size - 1 - col]
                result[size - 1 - row, size - 1 - col] = result[size - 1 - col, row]
                result[size - 1 - col, row] = temp
            }
        }
        return result
    }

    private fun <T : Any> Grid<T>.rotations(): Sequence<Grid<T>> {
        return generateSequence(this) { it.rotate() }.take(4)
    }

    private fun <T : Any> Grid<T>.permutations(): Sequence<Grid<T>> {
        return rotations().flatMap { it.reflections() }
    }

    private fun <T : Any> Grid<Grid<T>>.simplify(): Grid<T> {
        val innerSize = first().size
        val newSize = size * innerSize
        return Grid.ofValues(newSize, (0 until newSize).asSequence().flatMap { row ->
            (0 until newSize).asSequence().map { col ->
                val outerRow = row / innerSize
                val outerCol = col / innerSize
                val inner = get(outerRow, outerCol)
                inner[row % innerSize, col % innerSize]
            }
        })
    }

    private val initial = Grid.ofValues(3, sequenceOf(
            '.', '#', '.',
            '.', '.', '#',
            '#', '#', '#'
    ))

    private fun readRules(): Map<Grid<Char>, Rule> = openResource("Day21.txt").useLines { lines ->
        val result = mutableMapOf<Grid<Char>, Rule>()
        lines.map((Rule)::parse).forEach { rule ->
            rule.pattern.permutations().forEach { permutation ->
                result[permutation] = rule
            }
        }
        result
    }

    private fun Grid<Char>.transform(rules: Map<Grid<Char>, Rule>): Grid<Char> {
        return divide().map { region -> rules[region]!!.output }.simplify()
    }

    private fun solve(iterations: Int): Int {
        val rules = readRules()
        val final = generateSequence(initial) { it.transform(rules) }.take(iterations + 1).last()
        return final.count { it == '#' }
    }

    override fun solve() = Solution(
            part1 = solve(5),
            part2 = solve(18)
    )

}
