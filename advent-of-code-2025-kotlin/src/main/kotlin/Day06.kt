package com.fsg

import common.FileUtils

fun main() {
    val lines = readFile("/input06.txt")

    println("part 1: ${part1(lines)}")
    println("part 2: ${part2(lines)}")
}

private fun part1(maths: List<CephalopodMath>): Long {
    return maths.sumOf { operation ->
        val numbers = operation.values.map { it.trim().toLong() }
        when (operation.operator) {
            Operator.Plus -> numbers.sum()
            Operator.Multiplication -> numbers.reduce { acc, number -> acc * number }
        }
    }
}

private fun part2(maths: List<CephalopodMath>): Long {
    val advancedMaths = maths
        .map { op -> CephalopodMath(op.values.map { it.reversed() }, op.operator) }
        .map { op ->
            val numbers = mutableListOf<String>()
            (0 until op.values.first().length)
                .forEach { i -> numbers += op.values.map { it[i] }.filterNot { it.isWhitespace() }.joinToString("") }
            CephalopodMath(numbers, op.operator)
        }
    return part1(advancedMaths)
}

private fun readFile(fileName: String): List<CephalopodMath> {
    val allLines = FileUtils.getLines(fileName)
        .filter(String::isNotBlank)

    val operatorByIndex = allLines.last()
        .mapIndexed { index, char ->
            when (char) {
                '+' -> index to Operator.Plus
                '*' -> index to Operator.Multiplication
                else -> null
            }
        }.filterNotNull()
        .toMap()

    val lines = allLines.subList(0, allLines.size - 1)

    val indexes = operatorByIndex.keys.toList() + (lines.first().length + 1)
    return operatorByIndex.entries.mapIndexed { index, operationByIndex ->
        val start = operationByIndex.key
        val end = indexes[index + 1] - 1
        CephalopodMath(lines.map { it.substring(start, end) }, operationByIndex.value)
    }
}

private data class CephalopodMath(
    val values: List<String>,
    val operator: Operator
)

private enum class Operator { Plus, Multiplication }
