package wtf.log.xmas2017.days.day5

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day5 : Solver<Int, Int> {

    private fun readInput(): IntArray {
        return openResource("Day5.txt")
                .lineSequence()
                .map { it.toInt() }
                .toList()
                .toIntArray()
    }

    private fun part1(input: IntArray): Int {
        var pc = 0
        var steps = 0
        while (pc in input.indices) {
            val offset = input[pc]++
            pc += offset
            steps++
        }
        return steps
    }

    private fun part2(input: IntArray): Int {
        var pc = 0
        var steps = 0
        while (pc in input.indices) {
            val offset = input[pc]
            if (offset >= 3) {
                input[pc]--
            } else {
                input[pc]++
            }
            pc += offset
            steps++
        }
        return steps
    }

    override fun solve() = Solution(
            part1 = part1(readInput()),
            part2 = part2(readInput())
    )

}
