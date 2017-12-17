package wtf.log.xmas2017.days.day15

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver

object Day15 : Solver<Int, Int> {

    class Generator(val factor: Long, val divisor: Long, seed: Long) {

        private var state: Long = seed

        private fun step(): Long {
            val step = (state * factor) % Int.MAX_VALUE
            state = step
            return step
        }

        fun next(): Long {
            do step() while (state % divisor != 0L)
            return state
        }

    }

    private fun part1(): Int {
        val generatorA = Generator(factor = 16807, divisor = 1, seed = 289)
        val generatorB = Generator(factor = 48271, divisor = 1, seed = 629)
        return (0 until 40_000_000).sumBy {
            val a = generatorA.next() and 0xFFFF
            val b = generatorB.next() and 0xFFFF
            if (a == b) 1 else 0
        }
    }

    private fun part2(): Int {
        val generatorA = Generator(factor = 16807, divisor = 4, seed = 289)
        val generatorB = Generator(factor = 48271, divisor = 8, seed = 629)
        return (0 until 5_000_000).sumBy {
            val a = generatorA.next() and 0xFFFF
            val b = generatorB.next() and 0xFFFF
            if (a == b) 1 else 0
        }
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
