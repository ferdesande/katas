package org.fdesande

import kotlin.math.abs

class Day01 : AdventProblem {

    companion object {
        private const val FIRST_INPUT = "/input01-1.txt"
        private const val SECOND_INPUT = "/input01-1.txt"
    }

    override fun firstPart(): String {
        // Valid result: 3246517
        return readFile(FIRST_INPUT).first.sorted()
            .zip(readFile(FIRST_INPUT).second.sorted()) { first, second -> abs(first - second) }.sum().toString()
    }

    override fun secondPart(): String {
        // Valid result: 29379307
        val input = readFile(SECOND_INPUT)
        val countByNumber = input.second.groupingBy { it }.eachCount()

        return input.first.sumOf { item -> item * (countByNumber[item] ?: 0) }.toString()
    }

    private fun readFile(fileName: String): InputLists {
        val fileText = this.javaClass.getResource(fileName)?.readText()
            ?: throw IllegalStateException("File not found: $fileName")
        val readValues = fileText.split("\n")
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
