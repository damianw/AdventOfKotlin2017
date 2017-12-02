package wtf.log.xmas2017

/**
 * A solver for a problem.
 */
interface Solver<out A : Any, out B : Any> {

    fun solve(): Solution<A, B>

}

/**
 * A solution to a problem.
 * @property    part1   Result for part 1, if solved (else null)
 * @property    part2   Result for part 2, if solved (else null)
 */
data class Solution<out A : Any, out B : Any>(
        val part1: A?,
        val part2: B?
)
