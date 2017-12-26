package wtf.log.xmas2017.days.day23

import wtf.log.xmas2017.Solution
import wtf.log.xmas2017.Solver
import wtf.log.xmas2017.openResource
import kotlin.reflect.KClass

object Day23 : Solver<Long, Int> {

    sealed class Instruction {

        data class Send(val operand: Operand) : Instruction()
        data class Set(val register: Operand.Register, val operand: Operand) : Instruction()
        data class Add(val register: Operand.Register, val operand: Operand) : Instruction()
        data class Subtract(val register: Operand.Register, val operand: Operand) : Instruction()
        data class Multiply(val register: Operand.Register, val operand: Operand) : Instruction()
        data class Modulo(val register: Operand.Register, val operand: Operand) : Instruction()
        data class JumpPositive(val operand: Operand, val offset: Operand) : Instruction()
        data class JumpNonzero(val operand: Operand, val offset: Operand) : Instruction()
        data class Receive(val register: Operand.Register) : Instruction()

        companion object {

            fun parse(input: String): Instruction {
                val split = input.split(' ')
                return when (split.first()) {
                    "snd" -> Send(Operand.parse(split[1]))
                    "set" -> Set(Operand.parse(split[1]) as Operand.Register, Operand.parse(split[2]))
                    "add" -> Add(Operand.parse(split[1]) as Operand.Register, Operand.parse(split[2]))
                    "sub" -> Subtract(Operand.parse(split[1]) as Operand.Register, Operand.parse(split[2]))
                    "mul" -> Multiply(Operand.parse(split[1]) as Operand.Register, Operand.parse(split[2]))
                    "mod" -> Modulo(Operand.parse(split[1]) as Operand.Register, Operand.parse(split[2]))
                    "jgz" -> JumpPositive(Operand.parse(split[1]), Operand.parse(split[2]))
                    "jnz" -> JumpNonzero(Operand.parse(split[1]), Operand.parse(split[2]))
                    "rcv" -> Receive(Operand.parse(split[1]) as Operand.Register)
                    else -> throw IllegalArgumentException("Unknown instruction: $input")
                }
            }

        }

    }

    sealed class Operand {

        data class Register(val name: Char) : Operand()
        data class Literal(val value: Long) : Operand()

        companion object {

            fun parse(input: String): Operand = when (input.first()) {
                in 'a'..'z' -> Register(input.first())
                else -> Literal(input.toLong())
            }

        }

    }

    abstract class Machine(val program: List<Instruction>) {

        protected val registers = LongArray(26)
        private val opCounts = mutableMapOf<KClass<out Instruction>, Long>()

        protected val Operand.resolvedValue: Long
            get() = when (this) {
                is Operand.Register -> registers[index]
                is Operand.Literal -> value
            }

        protected val Operand.Register.index: Int
            get() = name.toInt() - 'a'.toInt()

        protected var Operand.Register.resolvedValue: Long
            get() = (this as Operand).resolvedValue
            set(value) {
                registers[index] = value
            }

        var isHalted = false
            protected set
        var pc = 0L
            protected set

        fun runToHalt() {
            while (!isHalted && pc in program.indices) {
                execute(program[pc.toInt()])
            }
            isHalted = true
        }

        fun opCount(kind: KClass<out Instruction>): Long = opCounts.getOrPut(kind) { 0L }

        inline fun <reified T : Instruction> opCount(): Long = opCount(T::class)

        protected fun execute(instruction: Instruction) = when (instruction) {
            is Instruction.Add -> binaryOp(instruction.register, instruction.operand, Long::plus)
            is Instruction.Subtract -> binaryOp(instruction.register, instruction.operand, Long::minus)
            is Instruction.Multiply -> binaryOp(instruction.register, instruction.operand, Long::times)
            is Instruction.Modulo -> binaryOp(instruction.register, instruction.operand, Long::rem)
            is Instruction.Send -> send(instruction.operand)
            is Instruction.Set -> set(instruction.register, instruction.operand)
            is Instruction.Receive -> receive(instruction.register)
            is Instruction.JumpPositive -> jumpPositive(instruction.operand, instruction.offset)
            is Instruction.JumpNonzero -> jumpNonzero(instruction.operand, instruction.offset)
        }.also { opCounts[instruction::class] = opCounts.getOrDefault(instruction::class, 0) + 1 }

        protected abstract fun send(operand: Operand)

        protected abstract fun receive(register: Operand.Register)

        private inline fun binaryOp(register: Operand.Register, operand: Operand, op: (Long, Long) -> Long) {
            register.resolvedValue = op(register.resolvedValue, operand.resolvedValue)
            pc++
        }

        private fun set(register: Operand.Register, operand: Operand) {
            register.resolvedValue = operand.resolvedValue
            pc++
        }

        private fun jumpPositive(operand: Operand, offset: Operand) {
            if (operand.resolvedValue > 0L) pc += offset.resolvedValue
            else pc++
        }

        private fun jumpNonzero(operand: Operand, offset: Operand) {
            if (operand.resolvedValue != 0L) pc += offset.resolvedValue
            else pc++
        }

    }

    class SimpleMachine(program: List<Instruction>, debug: Boolean = false) : Machine(program) {

        var output = 0L
            private set

        init {
            registers[0] = if (debug) 1L else 0L
        }

        override fun send(operand: Operand) {
            output = operand.resolvedValue
            pc++
        }

        override fun receive(register: Operand.Register) {
            if (register.resolvedValue != 0L) isHalted = true
            else pc++
        }

    }

    private fun readInput(): List<Instruction> = openResource("Day23.txt").useLines { lines ->
        lines.map((Instruction)::parse).toList()
    }

    private fun part1(): Long {
        val program = readInput()
        val machine = SimpleMachine(program)
        machine.runToHalt()
        return machine.opCount<Instruction.Multiply>()
    }

    private fun Long.isPrime(): Boolean = (2L..(this / 2L)).asSequence().all { this % it != 0L }

    private fun part2(): Int {
        val b = 105700L
        val c = 122700L
        return generateSequence(0L, Long::inc)
                .map { b + (it * 17L) }
                .takeWhile { it <= c }
                .filter { !it.isPrime() }
                .count()
    }

    override fun solve() = Solution(
            part1 = part1(),
            part2 = part2()
    )
}
