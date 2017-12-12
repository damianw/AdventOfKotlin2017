package wtf.log.xmas2017.days.day9

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import java.io.BufferedReader

object Day9 : Solver<Int, Int> {

    private inline fun <T> useInput(block: (BufferedReader) -> T): T {
        return openResource("Day9.txt").use(block)
    }

    private inline fun useChars(block: BufferedReader.(Char) -> Unit) {
        return useInput { reader -> reader.forEachChar(block) }
    }

    private inline fun BufferedReader.forEachChar(block: BufferedReader.(Char) -> Unit) {
        var value: Int = read()
        while (value != -1 && value != '\n'.toInt()) {
            block(value.toChar())
            value = read()
        }
    }

    interface Visitor {

        fun enterGroup() {}
        fun exitGroup() {}
        fun visitGarbage(char: Char) {}

    }

    private fun parse(visitor: Visitor) {
        var garbage = false
        useChars { char ->
            if (garbage) when (char) {
                '!' -> {
                    skip(1)
                }
                '>' -> {
                    garbage = false
                }
                else -> visitor.visitGarbage(char)
            } else when (char) {
                '!' -> {
                    skip(1)
                }
                '<' -> {
                    garbage = true
                }
                '>' -> {
                    garbage = false
                }
                '{' -> visitor.enterGroup()
                '}' -> visitor.exitGroup()
                ',' -> {
                }
                else -> throw IllegalArgumentException("Unknown char: $char")
            }
        }
    }

    class Part1Visitor : Visitor {

        private var depth = 0
        var sum: Int = 0
            private set

        override fun enterGroup() {
            depth++
        }

        override fun exitGroup() {
            sum += depth
            depth--
        }

    }

    fun part1(): Int = Part1Visitor().apply(this::parse).sum

    class Part2Visitor : Visitor {

        var sum: Int = 0
            private set

        override fun visitGarbage(char: Char) {
            sum++
        }

    }

    fun part2(): Int = Part2Visitor().apply(this::parse).sum

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
