package org.fdesande

import org.fdesande.Day07.Instruction.*
import org.fdesande.common.FileUtils

class Day07 : AdventProblem {

    companion object {
        private const val INPUT = "/input07.txt"
        private val INSTRUCTION_REGEX = Regex("(.+) -> (\\w+)")
    }

    override fun firstPart(): String {
        // Valid result: 956
        val instructionByWire = FileUtils.getLines(INPUT)
            .mapNotNull { line -> INSTRUCTION_REGEX.find(line) }
            .associate { match -> match.groupValues[2] to match.groupValues[1] }

        val circuit = Circuit(instructionByWire)
        return circuit.getWireValue("a")!!.toString()
    }

    override fun secondPart(): String {
        // Valid result: 40149
        val instructionByWire = FileUtils.getLines(INPUT)
            .mapNotNull { line -> INSTRUCTION_REGEX.find(line) }
            .associate { match -> match.groupValues[2] to match.groupValues[1] }
            .toMutableMap()

        val circuit = Circuit(instructionByWire)
        instructionByWire["b"] = circuit.getWireValue("a")!!.toString()

        val secondCircuit = Circuit(instructionByWire)
        return secondCircuit.getWireValue("a")!!.toString()
    }

    private class Circuit(private val instructionByWire: Map<String, String>) {
        companion object {
            private val VALUE_REGEX = Regex("^\\d+$")
            private val CONNECTION_REGEX = Regex("^\\w+$")
            private val AND_REGEX = Regex("^([a-z0-9]+) AND ([a-z0-9]+)")
            private val OR_REGEX = Regex("^([a-z0-9]+) OR ([a-z0-9]+)")
            private val L_SHIFT_REGEX = Regex("^([a-z0-9]+) LSHIFT (\\d+)")
            private val R_SHIFT_REGEX = Regex("^([a-z0-9]+) RSHIFT (\\d+)")
            private val NOT_REGEX = Regex("NOT ([a-z0-9]+)")
        }

        private val valueByWire = mutableMapOf<String, Int>()

        init {
            instructionByWire.keys.forEach { key -> solve(key) }
        }

        fun getWireValue(wire: String): Int? = valueByWire[wire]

        private fun solve(wire: String): Int {
            if (wire in valueByWire.keys) {
                return valueByWire[wire]!!
            }

            val valueMatch = VALUE_REGEX.find(wire)
            if (valueMatch != null) {
                return valueMatch.groupValues[0].toInt()
            }

            val value = when (val instruction = parseInstruction(wire)) {
                is And -> solve(instruction.wire1) and solve(instruction.wire2)
                is LShift -> solve(instruction.wire) shl instruction.shift
                is Not -> solve(instruction.wire).xor(UShort.MAX_VALUE.toInt())
                is Or -> solve(instruction.wire1) or solve(instruction.wire2)
                is RShift -> solve(instruction.wire) shr instruction.shift
                is Value -> instruction.value
                is Connection -> solve(instruction.wire)
            }

            valueByWire[wire] = value
            return value
        }

        private fun parseInstruction(wire: String): Instruction {
            val valueMatch = VALUE_REGEX.find(instructionByWire[wire] ?: "")
            val connectionMatch = CONNECTION_REGEX.find(instructionByWire[wire] ?: "")
            val andMatch = AND_REGEX.find(instructionByWire[wire] ?: "")
            val orMatch = OR_REGEX.find(instructionByWire[wire] ?: "")
            val lShiftMatch = L_SHIFT_REGEX.find(instructionByWire[wire] ?: "")
            val rShiftMatch = R_SHIFT_REGEX.find(instructionByWire[wire] ?: "")
            val notMatch = NOT_REGEX.find(instructionByWire[wire] ?: "")

            return when {
                valueMatch != null -> Value(valueMatch.groupValues[0].toInt())
                connectionMatch != null -> Connection(connectionMatch.groupValues[0])
                andMatch != null -> And(andMatch.groupValues[1], andMatch.groupValues[2])
                orMatch != null -> Or(orMatch.groupValues[1], orMatch.groupValues[2])
                lShiftMatch != null -> LShift(lShiftMatch.groupValues[1], lShiftMatch.groupValues[2].toInt())
                rShiftMatch != null -> RShift(rShiftMatch.groupValues[1], rShiftMatch.groupValues[2].toInt())
                notMatch != null -> Not(notMatch.groupValues[1])
                else -> throw IllegalArgumentException("Instruction for wire $wire could not be parsed")
            }
        }
    }

    private sealed class Instruction {
        class Value(val value: Int) : Instruction()
        class Connection(val wire: String) : Instruction()
        class And(val wire1: String, val wire2: String) : Instruction()
        class Or(val wire1: String, val wire2: String) : Instruction()
        class LShift(val wire: String, val shift: Int) : Instruction()
        class RShift(val wire: String, val shift: Int) : Instruction()
        class Not(val wire: String) : Instruction()
    }
}
