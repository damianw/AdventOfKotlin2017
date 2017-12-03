package wtf.log.xmas2017.days.day3

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import kotlin.math.abs
import kotlin.math.sqrt

object Day3 : Solver<Int, Int> {

    fun part1(input: Int): Int {
        val previousRoot = sqrt(input.toDouble()).toInt().let { sqrt ->
            if (sqrt % 2 == 0) sqrt - 1 else sqrt
        }
        val currentRoot = previousRoot + 2
        val perSide = currentRoot - 1
        val ringStart = previousRoot * previousRoot + 1
        val diff = input - ringStart
        val adjustedDistance = diff + 1
        val onSide = adjustedDistance % perSide
        val distanceToCenter = currentRoot / 2
        val distanceToCenterOfSide = abs(onSide - distanceToCenter)
        return distanceToCenter + distanceToCenterOfSide
    }

    override fun solve() = Solution(
            part1 = part1(361527),
            part2 = null
    )
}
