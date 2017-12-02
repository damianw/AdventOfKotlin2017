package wtf.log.xmas2016.days.day2

import wtf.log.xmas2016.Solution
import wtf.log.xmas2016.Solver
import wtf.log.xmas2016.openResource

object Day2 : Solver<Int, Int> {

    private fun readInput(): Sequence<List<Int>> {
        return openResource("Day2.txt")
                .lineSequence()
                .map { line ->
                    line.split('\t')
                            .map { it.toInt() }
                            .sortedDescending()
                }
    }

    private fun part1(): Int = readInput().sumBy { it.first() - it.last() }

    // I love "streamy" solutions :)
    private fun part2(): Int = readInput().sumBy { row ->
        row.asSequence()
                .withIndex()
                .map { (index, firstValue) ->
                    row.asSequence()
                            .drop(index + 1)
                            .firstOrNull { firstValue % it == 0 }
                            ?.let { firstValue / it }
                }
                .filterNotNull()
                .first()
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
