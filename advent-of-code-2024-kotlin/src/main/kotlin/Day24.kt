package org.fdesande

import org.fdesande.Day24.LogicValue.LogicEntry
import org.fdesande.Day24.LogicValue.LogicOperation
import org.fdesande.common.FileUtils
import java.util.*

class Day24 : AdventProblem {

    companion object {
        private const val INPUT = "/input24.txt"
    }

    override fun firstPart(): String {
        // Valid result: 55730288838374
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        return Circuit(lines).solve().toString()
    }

    override fun secondPart(): String {
        // Valid result: fvw,grf,mdb,nwq,wpq,z18,z22,z36
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val circuit = Circuit(lines)
        val swaps = mutableSetOf<String>()

        val operations = circuit.getOperationByKey().toMutableMap()
        val zOperationKeys = operations.keys.filter { it.startsWith('z') }.sorted().toMutableSet()

        // guard special operations
        guardZ00Wire(operations["z00"]!!)
        guardZ01Wire(operations["z01"]!!, operations)
        guardLastZWire(operations[zOperationKeys.last()]!!, operations)

        // get swaps for wrong wires and fix entries
        zOperationKeys.removeAll(setOf("z00", "z01", zOperationKeys.last()))

        findNonXorZWires(zOperationKeys, operations).forEach { (zKey, swapKey) ->
            val zEntry = operations[swapKey]!!
            val swapEntry = operations[zKey]!!

            operations[zKey] = zEntry
            operations[swapKey] = swapEntry
            swaps.add(zKey)
            swaps.add(swapKey)

            zOperationKeys.remove(zKey)
        }

        zOperationKeys.forEach { checkXorOperation(it, operations, swaps) }
        return swaps.toList().sorted().joinToString(",")
    }

    private fun findNonXorZWires(
        keys: Set<String>,
        operations: Map<String, LogicOperation>
    ): List<Pair<String, String>> =
        operations.entries
            .filter { it.key in keys }
            .filterNot { it.value.operation == Operation.XOR }.map { it.key }
            .map { key ->
                val xKey = key.replace('z', 'x')
                val yKey = key.replace('z', 'y')

                val expectedXor = LogicOperation(xKey, yKey, Operation.XOR)
                val currentXorKey = operations.entries.single { it.value == expectedXor }.key
                val currentWireKey = operations.entries
                    .single {
                        currentXorKey in setOf(it.value.entry1, it.value.entry2) &&
                            it.value.operation == Operation.XOR
                    }.key

                Pair(key, currentWireKey)
            }

    private fun checkXorOperation(
        key: String,
        operations: Map<String, LogicOperation>,
        swaps: MutableSet<String>
    ) {
        val op = operations[key]!!
        val bitNumber = key.replace("z", "").toInt()

        val expectedInputKey = getExpectedInputKey(bitNumber, operations)
        val expectedCarrierKey = getExpectedCarrierKey(bitNumber, operations)

        val keys = setOf(op.entry1, op.entry2)
        if (expectedCarrierKey == null) {
//            println("expected carrier key wrong for key $key")
        } else if (expectedInputKey !in keys && expectedCarrierKey !in keys) {
//            println("z wire wrong for key $key")
        } else if (expectedInputKey !in keys) {
            swaps.add(expectedInputKey)
            swaps.add(keys.single { it != expectedCarrierKey })
        } else if (expectedCarrierKey !in keys) {
            swaps.add(expectedCarrierKey)
            swaps.add(keys.single { it != expectedInputKey })
        }
    }

    private fun getExpectedInputKey(bitNumber: Int, operations: Map<String, LogicOperation>): String {
        val index = String.format("%02d", bitNumber)
        val expectedXor = LogicOperation("x$index", "y$index", Operation.XOR)
        return operations.entries.single { it.value == expectedXor }.key
    }

    private fun getExpectedCarrierKey(bitNumber: Int, operations: Map<String, LogicOperation>): String? {
        val index = String.format("%02d", bitNumber - 1)
        val expectedXor = LogicOperation("x$index", "y$index", Operation.XOR)
        val expectedAnd = LogicOperation("x$index", "y$index", Operation.AND)

        val xorKey = operations.entries.single { it.value == expectedXor }.key
        val andKey = operations.entries.single { it.value == expectedAnd }.key
        return operations.entries
            .singleOrNull {
                (it.value.entry1 == xorKey || it.value.entry2 == xorKey) && it.value.operation == Operation.AND
            }?.let { xorContainerKey ->
                val key = xorContainerKey.key
                operations.entries
                    .single { it.value == LogicOperation(andKey, key, Operation.OR) }.key
            }
    }

    private fun guardZ00Wire(op: LogicOperation) {
        if (op != LogicOperation("x00", "y00", Operation.XOR))
            throw IllegalStateException("z00 must be taken into account")
    }

    private fun guardZ01Wire(
        op: LogicOperation,
        operations: Map<String, LogicOperation>
    ) {
        val expectedOperations = setOf(
            LogicOperation("x01", "y01", Operation.XOR),
            LogicOperation("x00", "y00", Operation.AND),
        )

        if (operations[op.entry1] !in expectedOperations && operations[op.entry2] in expectedOperations)
            throw IllegalStateException("z01 must be taken into account")
    }

    private fun guardLastZWire(
        op: LogicOperation,
        operations: Map<String, LogicOperation>
    ) {
        if (op.operation != Operation.OR) {
            val expectedXor = LogicOperation("x44", "y44", Operation.XOR)
            val expectedAnd = LogicOperation("x44", "y44", Operation.AND)
            val op1 = operations[op.entry1]!!
            val op2 = operations[op.entry2]!!
            val subOps = if (op1 == expectedAnd) {
                Pair(operations[op2.entry1]!!, operations[op2.entry2]!!)
            } else if (op2 == expectedAnd) {
                Pair(operations[op1.entry1]!!, operations[op1.entry2]!!)
            } else null

            if (subOps == null || (expectedXor !in setOf(subOps.first, subOps.second))) {
                throw IllegalStateException("z45 must be taken into account")
            }
        }
    }

    private class Circuit(val lines: List<String>) {
        companion object {
            private val valueRegex = Regex("([a-z0-9]{3}): (\\d)")
            private val operationRegex = Regex("([a-z0-9]{3}) ([XORAND]{2,3}) ([a-z0-9]{3}) -> ([a-z0-9]{3})")
        }

        private val logicMap: Map<String, LogicValue> = parse()

        fun solve(): Long {
            val outputByName: MutableMap<String, Int> = mutableMapOf()
            val outputKeys = logicMap.keys
                .filter { entry -> entry.startsWith("z") }
                .toSet()

            logicMap
                .filter { entry -> entry.key in outputKeys }
                .forEach { entry ->
                    when (val operation = entry.value) {
                        is LogicEntry -> throw IllegalStateException("Element '${entry.key}' should be an operation")
                        is LogicOperation -> outputByName[entry.key] = expandOperation(operation, outputByName)
                    }
                }

            return outputKeys.sumOf { key ->
                val shift = key.substring(1).toInt()
                outputByName[key]!!.toLong() shl shift
            }
        }

        fun getOperationByKey(): Map<String, LogicOperation> {
            return lines
                .mapNotNull { line -> operationRegex.find(line) }.associate { match ->
                    match.groups[4]!!.value to
                        LogicOperation(
                            match.groups[1]!!.value,
                            match.groups[3]!!.value,
                            Operation.valueOf(match.groups[2]!!.value),
                        )
                }.toMutableMap()
        }

        private fun expandOperation(
            logicOperation: LogicOperation,
            outputByName: MutableMap<String, Int>
        ): Int {
            val entry1 = getEntryValue(logicOperation.entry1, outputByName)
            val entry2 = getEntryValue(logicOperation.entry2, outputByName)

            return when (logicOperation.operation) {
                Operation.AND -> entry1 and entry2
                Operation.XOR -> entry1 xor entry2
                Operation.OR -> entry1 or entry2
            }
        }

        private fun getEntryValue(entry: String, outputByName: MutableMap<String, Int>): Int {
            return if (entry in outputByName) {
                outputByName[entry]!!
            } else {
                outputByName[entry] = when (val value = logicMap[entry]!!) {
                    is LogicEntry -> if (value.value) 1 else 0
                    is LogicOperation -> expandOperation(value, outputByName)
                }
                outputByName[entry]!!
            }
        }

        private fun parse() =
            lines.mapNotNull { line ->
                val valueResult = valueRegex.find(line)
                val operationResult = operationRegex.find(line)
                if (valueResult != null) {
                    valueResult.groups[1]!!.value to LogicEntry(valueResult.groups[2]!!.value == "1")
                } else if (operationResult != null) {
                    operationResult.groups[4]!!.value to LogicOperation(
                        operationResult.groups[1]!!.value,
                        operationResult.groups[3]!!.value,
                        Operation.valueOf(operationResult.groups[2]!!.value)
                    )
                } else null
            }.toMap()
    }

    private enum class Operation { AND, XOR, OR }
    private sealed class LogicValue {
        data class LogicEntry(val value: Boolean) : LogicValue()

        data class LogicOperation(val entry1: String, val entry2: String, val operation: Operation) : LogicValue() {

            override fun equals(other: Any?): Boolean {
                if (other !is LogicOperation) return false

                val crossEntryKeyMatch = (other.entry1 == entry1 && other.entry2 == entry2)
                    || (other.entry2 == entry1 && other.entry1 == entry2)
                return other.operation == operation && crossEntryKeyMatch
            }

            override fun hashCode(): Int =
                if (entry1 < entry2)
                    Objects.hash(entry1, entry2, operation)
                else
                    Objects.hash(entry2, entry1, operation)
        }
    }
}
