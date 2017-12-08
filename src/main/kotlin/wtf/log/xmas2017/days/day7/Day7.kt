package wtf.log.xmas2017.days.day7

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import kotlin.LazyThreadSafetyMode.NONE

object Day7 : Solver<String, Int> {

    private class Tree private constructor() {

        interface Node {
            val name: String
            val weight: Int
            val parent: Node?
            val children: Set<Node>
            val cumulativeWeight: Int
        }

        val root: Node by lazy(NONE) {
            generateSequence<Node>(nodes.values.first()) { it.parent }.last()
        }

        private val nodes = mutableMapOf<String, NodeImpl>()

        private fun getNode(nodeName: String): NodeImpl = nodes.getOrPut(nodeName) { NodeImpl(nodeName) }

        private fun getNode(nodeSpec: NodeSpec): NodeImpl = getNode(nodeSpec.name).apply {
            weight = nodeSpec.weight
        }

        private fun insert(nodeSpec: NodeSpec) {
            val node = getNode(nodeSpec)
            for (childName in nodeSpec.childNames) {
                val child = getNode(childName)
                child.parent = node
                node.children.add(child)
            }
        }

        private data class NodeImpl(override val name: String) : Node {

            override var weight: Int = 0
            override var parent: Node? = null
            override val children = mutableSetOf<Node>()

            override val cumulativeWeight: Int by lazy(NONE) {
                children.sumBy { it.cumulativeWeight } + weight
            }

        }

        companion object {

            fun from(nodeSpecs: Iterable<NodeSpec>) = Tree().apply {
                nodeSpecs.forEach(this::insert)
            }

        }

    }

    data class NodeSpec(val name: String, val weight: Int, val childNames: List<String>) {

        companion object {

            private val PATTERN = Regex("([a-z]+) \\(([0-9]+)\\)(?: -> ((?:[a-z]+(?:, )?)+))?")

            fun parse(input: String): NodeSpec {
                val (name, weight, children) = PATTERN.matchEntire(input)!!.destructured
                return NodeSpec(
                        name = name,
                        weight = weight.toInt(),
                        childNames = children.split(", ")
                )
            }

        }

    }

    private fun constructTree(): Tree = openResource("Day7.txt").useLines { lines ->
        Tree.from(lines.map { NodeSpec.parse(it.trim()) }.asIterable())
    }

    private fun Tree.findIncorrectNode(): Tree.Node = generateSequence(root) { node ->
        node.findIncorrectChild()
    }.last()

    private fun Tree.Node.findIncorrectChild(): Tree.Node? {
        val counts = mutableMapOf<Int, Int>()
        val weights = mutableMapOf<Int, Tree.Node>()
        for (child in children) {
            val weight = child.cumulativeWeight
            val current = counts[weight] ?: 0
            counts[weight] = current + 1
            weights[weight] = child
        }
        return counts.entries.firstOrNull { it.value == 1 }?.key?.let(weights::get)
    }

    private fun part1(): String = constructTree().root.name

    private fun part2(): Int {
        val tree = constructTree()
        val node = tree.findIncorrectNode()
        val sibling = node.parent!!.children.first { it != node }
        val difference = sibling.cumulativeWeight - node.cumulativeWeight
        return node.weight + difference
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
