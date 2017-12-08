package wtf.log.xmas2017

import com.beust.jcommander.IValueValidator
import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import com.beust.jcommander.ParameterException
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import wtf.log.xmas2017.days.day1.Day1
import wtf.log.xmas2017.days.day2.Day2
import wtf.log.xmas2017.days.day3.Day3
import wtf.log.xmas2017.days.day4.Day4
import wtf.log.xmas2017.days.day5.Day5
import wtf.log.xmas2017.days.day6.Day6
import wtf.log.xmas2017.days.day7.Day7
import java.lang.Exception
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
        Day7
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

private fun <A : Any, B : Any> measureSeconds(solver: Solver<A, B>): Pair<Solution<A, B>, Long> {
    var result: Solution<A, B>? = null
    val time = measureNanoTime {
        result = solver.solve()
    }
    return result!! to (time / 1000000000)
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
        val (result, time) = measureSeconds(solver)
        val (part1, part2) = result
        Spinner.stop()
        println("-> Time elapsed: $time seconds")
        printIndented("-> Part 1: ", part1?.toString() ?: "<unsolved>")
        printIndented("-> Part 2: ", part2?.toString() ?: "<unsolved>")
        println()
    }

}
