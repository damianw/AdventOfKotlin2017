package wtf.log.xmas2017.days.day13

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day13 : Solver<Int, Int> {

    data class Layer(val depth: Int, val range: Int) {

        val severity: Int = depth * range

        private val lastIndex = range - 1

        fun getScannerPositionAtTime(time: Int): Int {
            val div = time / lastIndex
            val offset = time % lastIndex
            val reverse = div % 2 != 0
            return if (reverse) lastIndex - offset else offset
        }

        companion object {

            fun parse(input: String): Layer {
                val (depth, range) = input.split(": ")
                return Layer(depth.toInt(), range.toInt())
            }

        }

    }

    class Firewall(private val layers: Map<Int, Layer>) {

        val totalDepth: Int = layers.keys.max()!!

        private fun severities(delay: Int): Sequence<Int?> {
            return (0..totalDepth).asSequence().map { depth ->
                getSeverityAtTime(depth, time = depth + delay)
            }
        }

        fun getTotalSeverity(delay: Int): Int {
            return severities(delay)
                    .filterNotNull()
                    .sum()
        }

        fun canPassThrough(delay: Int): Boolean {
            return severities(delay).none { it != null }
        }

        fun getSeverityAtTime(depth: Int, time: Int): Int? {
            val layer = layers[depth] ?: return null
            val position = layer.getScannerPositionAtTime(time)
            return if (position == 0) layer.severity else null
        }

        companion object {

            fun fromLayers(layers: Iterable<Layer>) = Firewall(layers.associateBy { it.depth })

        }

    }

    private fun readFirewall(): Firewall = openResource("Day13.txt").useLines { lines ->
        Firewall.fromLayers(lines.map((Layer)::parse).asIterable())
    }

    fun part1(): Int = readFirewall().getTotalSeverity(delay = 0)

    fun part2(): Int {
        val firewall = readFirewall()
        return (0..Int.MAX_VALUE).first(firewall::canPassThrough)
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
