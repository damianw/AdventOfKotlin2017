package wtf.log.xmas2017.days.day8

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource

object Day8 : Solver<Int, Int> {

    private data class Instruction(
            val registerName: String,
            val kind: Kind,
            val amount: Int,
            val condition: Condition
    ) {

        fun apply(value: Int): Int = kind.apply(value, amount)

        enum class Kind(val symbol: String) {
            ADD(symbol = "inc") {
                override fun apply(value: Int, amount: Int): Int = value + amount
            },
            SUBTRACT(symbol= "dec") {
                override fun apply(value: Int, amount: Int): Int = value - amount
            };

            abstract fun apply(value: Int, amount: Int): Int

            companion object {
                fun fromSymbol(symbol: String) = values().first { it.symbol == symbol }
            }
        }

        companion object {

            private val PATTERN = Regex("([a-z]+) ([a-z]+) ((?:(?:-)?\\d)+) if ([a-z]+) ([<>=!]+) ((?:(?:-)?\\d)+)")

            fun parse(input: String): Instruction {
                val (register, kind, amount, argRegister, operator, value) = PATTERN.matchEntire(input)!!.destructured
                return Instruction(
                        registerName = register,
                        kind = Kind.fromSymbol(kind),
                        amount = amount.toInt(),
                        condition = Condition(
                                registerName = argRegister,
                                operator = Condition.Operator.fromSymbol(operator),
                                argument = value.toInt()
                        )
                )
            }

        }

    }

    private data class Condition(val registerName: String, val operator: Operator, val argument: Int) {

        fun isSatisfiedBy(value: Int) = operator.isSatisfiedBy(value.compareTo(argument))

        enum class Operator(val symbol: String) {
            GREATER_THAN(symbol = ">") {
                override fun isSatisfiedBy(comparisonResult: Int): Boolean = comparisonResult > 0
            },
            LESS_THAN(symbol = "<") {
                override fun isSatisfiedBy(comparisonResult: Int): Boolean = comparisonResult < 0
            },
            GREATER_THAN_OR_EQUAL_TO(symbol = ">=") {
                override fun isSatisfiedBy(comparisonResult: Int): Boolean = comparisonResult >= 0
            },
            LESS_THAN_OR_EQUAL_TO(symbol = "<=") {
                override fun isSatisfiedBy(comparisonResult: Int): Boolean = comparisonResult <= 0
            },
            EQUAL_TO(symbol = "==") {
                override fun isSatisfiedBy(comparisonResult: Int): Boolean = comparisonResult == 0
            },
            NOT_EQUAL_TO(symbol = "!=") {
                override fun isSatisfiedBy(comparisonResult: Int): Boolean = comparisonResult != 0
            };

            abstract fun isSatisfiedBy(comparisonResult: Int): Boolean

            companion object {
                fun fromSymbol(symbol: String) = values().first { it.symbol == symbol }
            }
        }

    }

    private class Machine {

        private val _registers = mutableMapOf<String, Int>()

        val registers: Map<String, Int>
            get() = _registers

        var maxValue: Int = 0
            private set

        fun execute(instruction: Instruction) {
            val condition = instruction.condition
            val conditionValue = _registers[condition.registerName] ?: 0
            if (condition.isSatisfiedBy(conditionValue)) {
                val registerName = instruction.registerName
                val registerValue = _registers[registerName] ?: 0
                val newValue = instruction.apply(registerValue)
                if (newValue > maxValue) {
                    maxValue = newValue
                }
                _registers[registerName] = newValue
            }
        }

        fun run(instructions: Iterable<Instruction>) {
            instructions.forEach(this::execute)
        }

    }

    private fun createAndRunMachine() = Machine().apply {
        openResource("Day8.txt").useLines { lines ->
            run(lines.map((Instruction)::parse).asIterable())
        }
    }

    private fun part1(): Int = createAndRunMachine().registers.values.max()!!

    private fun part2(): Int = createAndRunMachine().maxValue

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )

}
