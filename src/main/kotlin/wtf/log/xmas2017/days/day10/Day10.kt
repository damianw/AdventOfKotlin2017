package wtf.log.xmas2017.days.day10

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.util.Digests

object Day10 : Solver<Int, String> {

    private val INPUT = "187,254,0,81,169,219,1,190,19,102,255,56,46,32,2,216"

    private fun part1(): Int {
        val array = KnotDigest.RepeatingByteArray()
        val lengths = INPUT.split(',').map { it.toInt() }.toIntArray()
        var position = 0
        lengths.forEachIndexed { skip, length ->
            array.reverse(position, position + length)
            position += length + skip
        }
        return array[0] * array[1]
    }

    private fun part2(): String {
        val data = (INPUT.map { it.toByte() }).toByteArray()
        val hash = KnotDigest().digest(data)
        return Digests.toHexString(hash)
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
