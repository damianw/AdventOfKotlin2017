package wtf.log.xmas2017.days.day17

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.util.copyTo

object Day17 : Solver<Int, Int> {

    private fun part1(): Int {
        val stepSize = 370
        val iterations = 2017
        val buffer = IntArray(iterations + 1)
        var index = 0
        for (size in 1..iterations) {
            index = (index + stepSize + 1) % size
            buffer.copyTo(
                    destination = buffer,
                    sourcePosition = index,
                    destinationPosition = index + 1,
                    length = size - index
            )
            buffer[index] = size
        }
        return buffer[(index + 1) % buffer.size]
    }

    private fun part2(): Int {
        val stepSize = 370
        val iterations = 50_000_000
        var index = 0
        var inserted = 1
        for (size in 1..iterations) {
            index = (index + stepSize + 1) % size
            if (index == 0) {
                inserted = size
            }
        }
        return inserted
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
