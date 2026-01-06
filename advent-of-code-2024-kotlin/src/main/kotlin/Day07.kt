package org.fdesande

import org.fdesande.common.FileUtils
import kotlin.math.pow

class Day07 : AdventProblem {

    companion object {
        private const val INPUT = "/input07.txt"
    }

    override fun firstPart(): String {
        return getProblemEntries()
            .filter { problemEntry -> canSolve(problemEntry) }
            .sumOf { it.result }.toString()
    }

    override fun secondPart(): String {
        return getProblemEntries()
            .filter { problemEntry -> canSolve(problemEntry, 3) }
            .sumOf { it.result }.toString()
    }

    private fun canSolve(problemEntry: ProblemEntry, numberOfOperators: Int = 2): Boolean {
        return getOperators(problemEntry.values.size - 1, numberOfOperators)
            .any { operations -> canSolve(problemEntry, operations) }
    }

    private fun canSolve(problemEntry: ProblemEntry, operations: List<Operation>): Boolean {
        val total = problemEntry.values.reduceIndexed { index, acc, value ->
            if (acc > problemEntry.result) {
                return false
            }

            when (operations[index - 1]) {
                Operation.ADD -> acc + value
                Operation.MULTIPLY -> acc * value
                Operation.CONCATENATED -> (acc.toString() + value.toString()).toLong()
            }
        }

        return total == problemEntry.result
    }

    private fun getOperators(operationCount: Int, numberOfOperators: Int): List<List<Operation>> =
        (0 until numberOfOperators.toDouble().pow(operationCount).toLong())
            .map { i -> i.toString(numberOfOperators).padStart(operationCount, '0') }
            .map { value -> value.map { char -> Operation.getOperation(char) } }

    private fun getProblemEntries(): List<ProblemEntry> =
        FileUtils.getLines(INPUT)
            .filter { it.isNotEmpty() }
            .map { line -> line.split(":") }
            .map { parts ->
                Pair(
                    parts.first().toLong(),
                    parts.last().split(" ").filter { it.isNotEmpty() }.map { it.toLong() })
            }.map { ProblemEntry(it.first, it.second) }

    data class ProblemEntry(val result: Long, val values: List<Long>)
    enum class Operation {
        ADD, MULTIPLY, CONCATENATED;

        companion object {
            fun getOperation(char: Char) =
                when (char) {
                    '0' -> ADD
                    '1' -> MULTIPLY
                    '2' -> CONCATENATED
                    else -> throw IllegalArgumentException("$char cannot be converted to an operation")
                }
        }
    }
}
