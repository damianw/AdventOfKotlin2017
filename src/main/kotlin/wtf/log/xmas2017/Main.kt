package wtf.log.xmas2017

import com.beust.jcommander.IValueValidator
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import wtf.log.xmas2017.days.day10.Day10
import wtf.log.xmas2017.days.day1.Day1
import wtf.log.xmas2017.days.day11.Day11
import wtf.log.xmas2017.days.day12.Day12
import wtf.log.xmas2017.days.day13.Day13
import wtf.log.xmas2017.days.day14.Day14
import wtf.log.xmas2017.days.day15.Day15
import wtf.log.xmas2017.days.day16.Day16
import wtf.log.xmas2017.days.day17.Day17
import wtf.log.xmas2017.days.day19.Day19
import wtf.log.xmas2017.days.day2.Day2
import wtf.log.xmas2017.days.day20.Day20
import wtf.log.xmas2017.days.day21.Day21
import wtf.log.xmas2017.days.day22.Day22
import wtf.log.xmas2017.days.day23.Day23
import wtf.log.xmas2017.days.day24.Day24
import wtf.log.xmas2017.days.day25.Day25
import wtf.log.xmas2017.days.day3.Day3
import wtf.log.xmas2017.days.day4.Day4
import wtf.log.xmas2017.days.day5.Day5
import wtf.log.xmas2017.days.day6.Day6
import wtf.log.xmas2017.days.day7.Day7
import wtf.log.xmas2017.days.day8.Day8
import wtf.log.xmas2017.days.day9.Day9
import wtf.log.xmas2017.util.toPrettyFormat
import java.lang.Exception
import java.time.Duration
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess
import kotlin.system.measureNanoTime

private val SOLVERS = listOf<Solver<*, *>?>(
        Day1,
        Day2,
        Day3,
        Day4,
        Day5,
        Day6,
        Day7,
        Day8,
        Day9,
        Day10,
        Day11,
        Day12,
        Day13,
        Day14,
        Day15,
        Day16,
        Day17,
        null,
        Day19,
        Day20,
        Day21,
        Day22,
        Day23,
        Day24,
        Day25
)

private object ProgramArguments {

    @Parameter(
            names = ["--help", "-h"],
            description = "Prints usage information",
            help = true
    )
    var help: Boolean = false

    class DayValidator : IValueValidator<List<Int>> {
        override fun validate(name: String, value: List<Int>) {
            val errors = value.filter { SOLVERS.getOrNull(it - 1) == null }.distinct()
            // the lesson here is: never build sentences in code, kids
            when (errors.size) {
                0 -> return
                1 -> throw ParameterException("I haven't implemented day ${errors.single()}!")
                2 -> throw ParameterException("I haven't implemented days ${errors[0]} or ${errors[1]}!")
                else -> {
                    val joined = errors.joinToString(limit = errors.lastIndex, truncated = "or ${errors.last()}")
                    throw ParameterException("I haven't implemented days $joined!")
                }
            }
        }
    }

    @Parameter(
            names = ["--days", "-d"],
            description = "Days of the advent calendar to solve",
            validateValueWith = [DayValidator::class]
    )
    var days: List<Int>? = null

}

private object Spinner {

    private val spinBars = charArrayOf('-', '\\', '|', '/', '-', '\\', '|', '/')

    private var subscription: Disposable? = null

    fun start() {
        subscription?.dispose()
        subscription = Flowable.interval(100, TimeUnit.MILLISECONDS)
                .map { spinBars[it.toInt() % spinBars.size] }
                .subscribe { print("\r$it ") }
    }

    fun stop() {
        subscription?.dispose()
        print('\r')
    }

}

private fun <A : Any, B : Any> measureDuration(solver: Solver<A, B>): Pair<Solution<A, B>, Duration> {
    var result: Solution<A, B>? = null
    val nanos = measureNanoTime {
        result = solver.solve()
    }
    val duration = Duration.ofNanos(nanos)
    return result!! to duration
}

private fun String.indent(amount: Int): String = buildString {
    for (i in 0 until amount) {
        append(' ')
    }
    append(this@indent)
}

private fun printIndented(header: String, input: String) {
    print(header)
    val lines = input.lines()
    println(lines.first())
    lines.drop(1).map { it.indent(header.length) }.forEach(::println)
}

fun main(args: Array<String>) {
    val commander = JCommander(ProgramArguments).apply {
        programName = "AdventOfKotlin2017"
    }

    fun exitWithUsage(message: String? = null): Nothing {
        message?.let { System.err.println("[Error] $it") }
        commander.usage()
        exitProcess(1)
    }

    try {
        commander.parse(*args)
    } catch (e: Exception) {
        exitWithUsage(e.message)
    }

    if (ProgramArguments.help) {
        exitWithUsage()
    }

    val days = ProgramArguments.days ?: 1..SOLVERS.size

    for (day in days) {
        val solver = SOLVERS[day - 1] ?: continue
        println("========")
        println("Day $day")
        println("========")
        Spinner.start()
        val (result, duration) = measureDuration(solver)
        val (part1, part2) = result
        Spinner.stop()
        println("-> Time elapsed: ${duration.toPrettyFormat()}")
        printIndented("-> Part 1: ", part1?.toString() ?: "<unsolved>")
        printIndented("-> Part 2: ", part2?.toString() ?: "<unsolved>")
        println()
    }

}
