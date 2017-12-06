package wtf.log.xmas2017.days.day6

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day6 : Solver<Int, Int> {

    private fun rotationSequence(initialValue: Int, size: Int): Sequence<Int> {
        return generateSequence(initialValue - 1) { value ->
            val next = value + 1
            if (next >= size) 0 else next
        }.drop(1)
    }

    private data class MemoryState(val bankSizes: List<Int>) {

        fun redistribute(): MemoryState {
            val newBankSizes = bankSizes.toIntArray()
            val (initialIndex, maxValue) = bankSizes.withIndex().maxBy { it.value }!!
            newBankSizes[initialIndex] = 0
            rotationSequence(initialIndex + 1, bankSizes.size)
                    .take(maxValue)
                    .forEach { index ->
                        newBankSizes[index]++
                    }
            return MemoryState(newBankSizes.toList())
        }

    }

    private fun readInput() = MemoryState(
            bankSizes = openResource("Day6.txt").readText().trim().split('\t').map { it.toInt() }
    )

    private fun part1(): Int {
        var currentState = readInput()
        val previousStates = mutableSetOf<MemoryState>()
        var count = 0
        while (previousStates.add(currentState)) {
            currentState = currentState.redistribute()
            count++
        }
        return count
    }

    private fun part2(): Int {
        var currentState = readInput()
        val previousStates = mutableMapOf<MemoryState, Int>()
        var count = 0
        while (currentState !in previousStates) {
            previousStates[currentState] = count
            currentState = currentState.redistribute()
            count++
        }
        return count - previousStates[currentState]!!
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
