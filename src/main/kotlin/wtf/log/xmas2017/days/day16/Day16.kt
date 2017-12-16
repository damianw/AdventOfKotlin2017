package wtf.log.xmas2017.days.day16

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import wtf.log.xmas2017.util.collectTo
import wtf.log.xmas2017.util.copyTo

object Day16 : Solver<String, String> {

    private sealed class DanceMove {

        abstract fun perform(dancers: CharArray, buffer: CharArray)

        data class Spin(val amount: Int) : DanceMove() {
            override fun perform(dancers: CharArray, buffer: CharArray) {
                val offset = dancers.size - amount
                dancers.copyTo(buffer, sourcePosition = offset, length = amount)
                dancers.copyTo(dancers, destinationPosition = amount, length = offset)
                buffer.copyTo(dancers, length = amount)
            }
        }

        data class Exchange(val firstIndex: Int, val secondIndex: Int) : DanceMove() {
            override fun perform(dancers: CharArray, buffer: CharArray) {
                val first = dancers[firstIndex]
                dancers[firstIndex] = dancers[secondIndex]
                dancers[secondIndex] = first
            }
        }

        data class Partner(val firstDancer: Char, val secondDancer: Char) : DanceMove() {
            override fun perform(dancers: CharArray, buffer: CharArray) {
                val firstIndex = dancers.indexOf(firstDancer)
                val secondIndex = dancers.indexOf(secondDancer)
                val first = dancers[firstIndex]
                dancers[firstIndex] = dancers[secondIndex]
                dancers[secondIndex] = first
            }
        }

        companion object {

            fun parse(input: String): DanceMove = when (input[0]) {
                's' -> Spin(input.substring(1).toInt())
                'x' -> input.substring(1).split('/').let { (a, b) ->
                    Exchange(firstIndex = a.toInt(), secondIndex = b.toInt())
                }
                'p' -> Partner(input[1], input[3])
                else -> throw IllegalArgumentException("Unknown move: $input")
            }

        }

    }

    private class Stage(size: Int = SIZE_DEFAULT) {

        private val dancers = CharArray(size) { (CHAR_START + it).toChar() }
        private val buffer = CharArray(size)

        fun update(move: DanceMove) = move.perform(dancers, buffer)

        // now with automatic loop detection!
        fun update(routine: DanceRoutine) {
            val iterations = mutableMapOf<String, Int>()
            val transformations = mutableMapOf<String, String>()
            var loopLength = 0
            var iteration = 0
            while (iteration < routine.iterations) {
                val currentState = String(dancers)
                val previousIteration = iterations[currentState]
                if (previousIteration != null) {
                    loopLength = iteration - previousIteration
                    break
                }

                routine.danceMoves.forEach(this::update)
                val nextState = String(dancers)
                iterations[currentState] = iteration
                transformations[currentState] = nextState
                iteration++
            }

            if (loopLength > 0) repeat(times = (routine.iterations - iteration) % loopLength) {
                transformations[String(dancers)]!!.toCharArray(dancers)
            }
        }

        override fun toString() = String(dancers)

        companion object {

            const val CHAR_START = 'a'.toInt()
            const val SIZE_DEFAULT = 16

        }

    }

    private data class DanceRoutine(val danceMoves: List<DanceMove>, val iterations: Int)

    private fun readInput(): Sequence<DanceMove> {
        return openResource("Day16.txt").use { it.readText().trim() }
                .splitToSequence(',')
                .map((DanceMove)::parse)
    }

    private fun part1(): String = readInput().collectTo(Stage(), Stage::update).toString()

    private fun part2(): String {
        val stage = Stage()
        val moves = readInput().toList()
        val routine = DanceRoutine(moves, iterations = 1_000_000_000)
        stage.update(routine)
        return stage.toString()
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
