package wtf.log.xmas2017.days.day25

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day25 : Solver<Int, String> {

    private fun Int.toBoolean(): Boolean = when (this) {
        0 -> false
        1 -> true
        else -> throw IllegalArgumentException("Not a boolean value: $this")
    }

    class Machine(val states: Map<Char, State>, val initialState: Char, val iterations: Int) {

        data class State(val symbol: Char, val trueAction: Action, val falseAction: Action)
        data class Action(val value: Boolean, val direction: Direction, val next: Char)
        enum class Direction {
            LEFT,
            RIGHT;

            companion object {
                fun parse(input: String): Direction = valueOf(input.toUpperCase())
            }
        }

        class Node(left: Node? = null, right: Node? = null) {
            var value: Boolean = false
            private val _left = lazy { left ?: Node(right = this) }
            private val _right = lazy { right ?: Node(left = this) }
            val left: Node by _left
            val right: Node by _right
            val safeLeft: Node? get() = if (_left.isInitialized()) left else null
            val safeRight: Node? get() = if (_right.isInitialized()) right else null
        }

        fun execute(): Node {
            val root = Node()
            var node = root
            var state = states[initialState]!!
            repeat(iterations) {
                val action = if (node.value) state.trueAction else state.falseAction
                node.value = action.value
                node = when (action.direction) {
                    Direction.LEFT -> node.left
                    Direction.RIGHT -> node.right
                }
                state = states[action.next]!!
            }
            return root
        }

        fun checksum(): Int {
            val root = execute()
            val nodes = generateSequence(root, Node::safeLeft) + generateSequence(root, Node::safeRight).drop(1)
            return nodes.filter { it.value }.count()
        }

        companion object {

            private const val NUM = "(\\d+)"
            private const val STATE = "([A-Z])"
            private const val DIRECTION = "(left|right)"

            private val PATTERN_HEADER = Regex("""
                Begin in state $STATE.
                Perform a diagnostic checksum after $NUM steps.
            """.trimIndent())

            private val PATTERN_STATE = Regex("""
                In state $STATE:
                  If the current value is 0:
                    - Write the value $NUM.
                    - Move one slot to the $DIRECTION.
                    - Continue with state $STATE.
                  If the current value is 1:
                    - Write the value $NUM.
                    - Move one slot to the $DIRECTION.
                    - Continue with state $STATE.
            """.trimIndent())

            private fun offsetOf(direction: String): Int = when (direction) {
                "left" -> -1
                "right" -> +1
                else -> throw IllegalArgumentException("Unknown direction: $direction")
            }

            fun parse(input: String): Machine {
                val split = input.trim().split("\n\n")
                val (startState, iterations) = PATTERN_HEADER.matchEntire(split.first())!!.destructured
                val states = split.asSequence()
                        .drop(1)
                        .map { block ->
                            val (symbol, falseValue, falseDirection, falseNext, trueValue,
                                    trueDirection, trueNext) = PATTERN_STATE.matchEntire(block)!!.destructured
                            State(
                                    symbol = symbol.single(),
                                    trueAction = Action(
                                            value = trueValue.toInt().toBoolean(),
                                            direction = Direction.parse(trueDirection),
                                            next = trueNext.single()
                                    ),
                                    falseAction = Action(
                                            value = falseValue.toInt().toBoolean(),
                                            direction = Direction.parse(falseDirection),
                                            next = falseNext.single()
                                    )
                            )
                        }
                        .associateBy { it.symbol }
                return Machine(
                        states = states,
                        initialState = startState.single(),
                        iterations = iterations.toInt()
                )
            }

        }

    }

    private fun readMachine(): Machine = Machine.parse(openResource("Day25.txt").use { it.readText() })

    private fun part1(): Int = readMachine().checksum()

    override fun solve() = Solution(
            part1 = part1(),
            part2 = "Merry Christmas! <3"
    )

}
