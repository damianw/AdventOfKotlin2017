package wtf.log.xmas2017.days.day4

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day4 : Solver<Int, Int> {

    private fun readInput(): Sequence<List<String>> {
        return openResource("Day4.txt")
                .lineSequence()
                .map { it.split(' ') }
    }

    private fun part1(): Int = readInput().count { it.distinct() == it }

    private fun part2(): Int = readInput()
            .map { words ->
                words.map { it.asSequence().sorted().joinToString("") }
            }
            .count { it.distinct() == it }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
