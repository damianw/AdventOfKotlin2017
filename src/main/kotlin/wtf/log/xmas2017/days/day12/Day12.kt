package wtf.log.xmas2017.days.day12

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day12 : Solver<Int, Int> {

    class Graph private constructor() {

        private val nodes = mutableMapOf<Int, NodeImpl>()

        interface Node {
            val id: Int
            val connected: Set<Node>
        }

        operator fun get(nodeId: Int): Node? = nodes[nodeId]

        fun nodes(): Set<Node> = nodes.values.toSet()

        private fun obtainNode(id: Int): NodeImpl {
            return nodes.getOrPut(id) { NodeImpl(id) }
        }

        private fun insert(nodeSpec: NodeSpec) {
            val node = obtainNode(nodeSpec.nodeId)
            for (nodeId in nodeSpec.connections) {
                val connected = obtainNode(nodeId)
                node.connected.add(connected)
                connected.connected.add(node)
            }
        }

        private data class NodeImpl(override val id: Int) : Node {
            override val connected = mutableSetOf<NodeImpl>()
        }

        companion object {

            fun from(nodeSpecs: Iterable<NodeSpec>) = Graph().apply {
                nodeSpecs.forEach(this::insert)
            }

        }

    }

    data class NodeSpec(val nodeId: Int, val connections: List<Int>) {

        companion object {

            private val PATTERN = Regex("([\\d]+) <-> ([\\d, ]+)")

            fun parse(input: String): NodeSpec {
                val (nodeId, connections) = PATTERN.matchEntire(input)!!.destructured
                return NodeSpec(
                        nodeId = nodeId.toInt(),
                        connections = connections.split(", ").map { it.toInt() }
                )
            }

        }

    }

    private fun constructGraph(): Graph {
        return openResource("Day12.txt").useLines { lines ->
            Graph.from(lines.map((NodeSpec)::parse).asIterable())
        }
    }

    private fun Graph.Node.group(result: MutableSet<Graph.Node> = mutableSetOf()): Set<Graph.Node> {
        if (this in result) return result
        result.add(this)
        for (node in connected) {
            node.group(result)
        }
        return result
    }

    private fun part1(): Int {
        val graph = constructGraph()
        val zero = graph[0]!!
        val connected = zero.group()
        return connected.size
    }

    private fun part2(): Int {
        val graph = constructGraph()
        val nodes = graph.nodes().toMutableSet()
        var groupCount = 0
        while (nodes.isNotEmpty()) {
            val node = nodes.first()
            nodes.removeAll(node.group())
            groupCount++
        }
        return groupCount
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )
}
