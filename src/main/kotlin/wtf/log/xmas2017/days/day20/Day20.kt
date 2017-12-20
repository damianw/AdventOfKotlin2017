package wtf.log.xmas2017.days.day20

import com.google.common.collect.HashBiMap
import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import kotlin.math.absoluteValue
import kotlin.math.sign

object Day20 : Solver<Int, Int> {

    data class Vector3(val x: Int, val y: Int, val z: Int) : Comparable<Vector3> {

        val manhattanMagnitude: Int = x.absoluteValue + y.absoluteValue + z.absoluteValue

        override fun compareTo(other: Vector3): Int {
            return manhattanMagnitude.compareTo(other.manhattanMagnitude)
        }

        operator fun plus(other: Vector3) = Vector3(
                x = x + other.x,
                y = y + other.y,
                z = z + other.z
        )

        @Suppress("NOTHING_TO_INLINE")
        private inline fun Int.signEquals(other: Int): Boolean {
            return this == 0 || other == 0 || this == other
        }

        fun signEquals(other: Vector3): Boolean {
            return this.x.signEquals(other.x) && this.y.signEquals(other.y) && this.z.signEquals(other.z)
        }

        fun signum() = Vector3(x = x.sign, y = y.sign, z = z.sign)

    }

    private data class Particle(
            val id: Int,
            val position: Vector3,
            val velocity: Vector3,
            val acceleration: Vector3
    ) {

        override fun toString(): String = "Particle(id=$id)"

        companion object {

            private const val NUM = "((?:-)?\\d+)"
            private val PATTERN = Regex("p=<$NUM,$NUM,$NUM>, v=<$NUM,$NUM,$NUM>, a=<$NUM,$NUM,$NUM>")

            fun parse(id: Int, input: String): Particle {
                val (px, py, pz, vx, vy, vz, ax, ay, az) = PATTERN.matchEntire(input)!!.destructured
                return Particle(
                        id = id,
                        position = Vector3(x = px.toInt(), y = py.toInt(), z = pz.toInt()),
                        velocity = Vector3(x = vx.toInt(), y = vy.toInt(), z = vz.toInt()),
                        acceleration = Vector3(x = ax.toInt(), y = ay.toInt(), z = az.toInt())
                )
            }

        }

    }

    private class ParticleSystem(particles: List<Particle>) {

        private val positions = HashBiMap.create(particles.associate { it to it.position })
        private val velocities = particles.associateTo(mutableMapOf()) { it to it.velocity }
        private val buffer = positions.toMutableMap()
        private val collisions = mutableSetOf<Vector3>()

        private fun step() {
            for ((particle, previousVelocity) in velocities) {
                velocities[particle] = previousVelocity + particle.acceleration
            }
            buffer.clear()
            buffer.putAll(positions)
            positions.clear()
            collisions.clear()
            val inverse = positions.inverse()
            for ((particle, previousPosition) in buffer) {
                val newPosition = previousPosition + velocities[particle]!!
                if (newPosition in collisions) {
                    inverse.remove(newPosition)
                } else {
                    inverse[newPosition] = particle
                    collisions.add(newPosition)
                }
            }
        }

        private val Particle.isTerminal: Boolean
            get() = positions[this]!!.signum().signEquals((velocities[this]!!.signum() + acceleration.signum()).signum())

        fun resolve(): Set<Particle> {
            while (positions.any { (particle) -> !particle.isTerminal }) {
                step()
            }
            return positions.keys
        }

    }

    private fun readInput(): List<Particle> = openResource("Day20.txt").useLines { lines ->
        lines.withIndex().map { (i, line) -> Particle.parse(i, line) }.toList()
    }

    private fun part1(): Int {
        return readInput().minBy { it.acceleration }!!.id
    }

    private fun part2(): Int {
        val system = ParticleSystem(readInput())
        val result = system.resolve()
        return result.size
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
