package wtf.log.xmas2017.days.day1

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day1 : Solver<Int, Int> {

    /**
     * Returns a sequence of overlapping pairwise elements, also overlapping the first one at the end.
     */
    fun <T : Any> Sequence<T>.withNext(): Sequence<Pair<T, T>> = iterator().let { iterator ->
        if (!iterator.hasNext()) return emptySequence()
        val first = iterator.next()
        var previous = first
        var complete = false
        generateSequence {
            if (!iterator.hasNext()) {
                if (complete) null
                else {
                    complete = true
                    Pair(previous, first)
                }
            } else Pair(previous, iterator.next()).apply { previous = second }
        }
    }

    /**
     * Returns a sequence of elements paired with the element halfway around the list.
     */
    private fun IntArray.withRotationPairs(): List<Pair<Int, Int>> {
        require(size % 2 == 0)
        val rotation = size / 2
        return mapIndexed { index, value ->
            Pair(value, this[(index + rotation) % size])
        }
    }

    /**
     * Reads the input file.
     */
    private fun readInput(): IntArray {
        return openResource("Day1.txt").readText()
                .trim()
                .map { Character.digit(it, 10) }
                .toIntArray()
    }

    private fun part1(): Int {
        return readInput()
                .asSequence()
                .withNext()
                .sumBy { (current, next) ->
                    if (next == current) current else 0
                }
    }

    private fun part2(): Int {
        return readInput().withRotationPairs()
                .sumBy { (current, next) ->
                    if (next == current) current else 0
                }
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
