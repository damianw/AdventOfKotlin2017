package wtf.log.xmas2017.days.day24

import com.google.common.collect.HashMultimap
import com.google.common.collect.ImmutableMultimap
import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import wtf.log.xmas2017.util.alter

object Day24 : Solver<Int, Int> {

    class Component(val start: Int, val end: Int) {

        val strength: Int = start + end

        override fun toString() = "$start/$end"

        companion object {
            fun parse(input: String): Component {
                val (start, end) = input.split('/')
                return Component(start.toInt(), end.toInt())
            }
        }

    }

    private fun readInput(): ImmutableMultimap<Int, Component> {
        val result = HashMultimap.create<Int, Component>()
        openResource("Day24.txt").useLines { lines ->
            lines.map((Component)::parse).forEach { component ->
                result.put(component.start, component)
                result.put(component.end, component)
            }
        }
        return ImmutableMultimap.copyOf(result)
    }

    private fun ImmutableMultimap<Int, Component>.paths(start: Int): Sequence<Sequence<Component>> = get(start)
            .asSequence()
            .flatMap { component ->
                val bin = alter {
                    remove(component.start, component)
                    remove(component.end, component)
                }
                val next = if (component.start == start) component.end else component.start
                sequenceOf(sequenceOf(component)) + bin.paths(next).map { sequenceOf(component) + it }
            }

    private fun <T> Sequence<Sequence<T>>.toLists(): List<List<T>> = map { it.toList() }.toList()

    private fun part1(paths: List<List<Component>>): Int {
        return paths.asSequence()
                .map { path -> path.sumBy { it.strength } }
                .max()!!
    }

    private fun part2(paths: List<List<Component>>): Int {
        val comparator = Comparator
                .comparing<List<Component>, Int>(List<*>::size)
                .thenComparing<Int> { path -> path.sumBy(Component::strength) }
                .reversed()
        return paths.sortedWith(comparator).first().sumBy { it.strength }
    }

    override fun solve(): Solution<Int, Int> {
        val bridges = readInput().paths(start = 0).toLists()
        return Solution(
                part1 = part1(bridges),
                part2 = part2(bridges)
        )
    }

}
