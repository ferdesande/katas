package org.fdesande

import org.fdesande.common.FileUtils
import kotlin.math.abs

class Day01 : AdventProblem {

    companion object {
        private const val INPUT = "/input01.txt"
    }

    override fun firstPart(): String {
        return readFile(INPUT).first.sorted()
            .zip(readFile(INPUT).second.sorted()) { first, second -> abs(first - second) }.sum().toString()
    }

    override fun secondPart(): String {
        val input = readFile(INPUT)
        val countByNumber = input.second.groupingBy { it }.eachCount()

        return input.first.sumOf { item -> item * (countByNumber[item] ?: 0) }.toString()
    }

    private fun readFile(fileName: String): InputLists {
        val readValues = FileUtils.getLines(fileName)
            .filter { line -> line.isNotBlank() }
            .map { line -> line.split(Regex("\\s+")) }
            .map { items -> items.map { item -> item.toInt() } }

        val first = readValues.map { it[0] }
        val second = readValues.map { it[1] }

        return InputLists(first, second)
    }

    private data class InputLists(
        val first: List<Int>,
        val second: List<Int>,
    )
}
