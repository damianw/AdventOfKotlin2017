package wtf.log.xmas2017.days.day19

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day19 : Solver<String, Int> {

    private class CharGrid(private val lines: List<String>) {

        operator fun get(x: Int, y: Int): Char? {
            if (y !in lines.indices) return null
            val line = lines[y]
            if (x !in line.indices) return null
            val char = line[x]
            return if (char == ' ') null else char
        }

        operator fun get(point: Point): Char? {
            return get(point.x, point.y)
        }

        operator fun contains(point: Point): Boolean {
            return get(point) != null
        }

        fun find(char: Char, y: Int): Point {
            return Point(x = lines[y].indexOf(char), y = y)
        }

    }

    private data class Point(val x: Int, val y: Int) {

        fun inDirection(direction: Direction) = Point(x = x + direction.dx, y = y + direction.dy)

    }

    private enum class Direction(val dx: Int = 0, val dy: Int = 0) {
        LEFT(dx = -1),
        UP(dy = -1),
        RIGHT(dx = 1),
        DOWN(dy = 1);

        val reverse: Direction
            get() = when (this) {
                LEFT -> RIGHT
                UP -> DOWN
                RIGHT -> LEFT
                DOWN -> UP
            }
    }

    private class NetworkMap(private val charGrid: CharGrid) {

        val root: Node = NodeImpl(
                point = charGrid.find(Token.VerticalPath.symbol, y = 0),
                travelDirection = Direction.DOWN
        )

        fun path(): Sequence<Node> = generateSequence(root, Node::next)

        sealed class Token(val symbol: Char) {
            object VerticalPath : Token(symbol = '|')
            object HorizontalPath : Token(symbol = '-')
            object Turn : Token(symbol = '+')
            class Element(symbol: Char) : Token(symbol)

            companion object {
                fun match(symbol: Char): Token = when (symbol) {
                    VerticalPath.symbol -> VerticalPath
                    HorizontalPath.symbol -> HorizontalPath
                    Turn.symbol -> Turn
                    else -> Element(symbol)
                }
            }
        }

        interface Node {
            val point: Point
            val token: Token
            fun next(): Node?
        }

        private inner class NodeImpl(override val point: Point, val travelDirection: Direction) : Node {

            override val token: Token = Token.match(charGrid[point]!!)

            override fun next(): Node? = when (token) {
                is Token.VerticalPath,
                is Token.HorizontalPath,
                is Token.Element -> point.inDirection(travelDirection).takeIf(charGrid::contains)?.let {
                    NodeImpl(it, travelDirection)
                }
                is Token.Turn -> {
                    val (direction, nextPoint) = Direction.values()
                            .asSequence()
                            .filter { it.reverse != travelDirection }
                            .map { it to point.inDirection(it) }
                            .single { (_, nextPoint) -> charGrid[nextPoint] != null }
                    NodeImpl(nextPoint, direction)
                }
            }

            override fun toString(): String {
                return "Node(point=$point, travelDirection=$travelDirection, token=$token)"
            }

        }

    }

    private fun readNetwork(): NetworkMap {
        return NetworkMap(charGrid = CharGrid(openResource("Day19.txt").use { it.readLines() }))
    }

    private fun part1(): String {
        val network = readNetwork()
        return network.path().mapNotNull { (it.token as? NetworkMap.Token.Element)?.symbol }.joinToString("")
    }

    private fun part2(): Int {
        val network = readNetwork()
        return network.path().count()
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
