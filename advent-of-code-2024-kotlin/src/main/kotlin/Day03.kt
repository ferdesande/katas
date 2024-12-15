package org.fdesande

import org.fdesande.common.FileUtils

class Day03 : AdventProblem {

    companion object {
        private const val FIRST_INPUT = "/input03-1.txt"
        private const val SECOND_INPUT = "/input03-2.txt"
    }

    override fun firstPart(): String {
        // Valid result: 159833790
        return extractMultipliers(
            readFile(FIRST_INPUT)
        ).sumOf { multipliers -> multipliers[0] * multipliers[1] }.toString()
    }

    override fun secondPart(): String {
        // Valid result: 89349241
        return readFile(SECOND_INPUT)
            .joinToString("\\n")
            .split(Regex("don't\\(\\).*?do\\(\\)")) // Hint: .*? is a no greedy .*
            .let { lines -> extractMultipliers(lines) }
            .sumOf { multipliers -> multipliers[0] * multipliers[1] }.toString()
    }

    private fun readFile(fileName: String): List<String> {
        return FileUtils.getLines(fileName)
            .filter { line -> line.isNotBlank() }
    }

    private fun extractMultipliers(lines: List<String>): List<List<Int>> {
        return lines
            .map { line -> Regex("mul\\(\\d+,\\d+\\)").findAll(line).map { it.value }.toList() }
            .flatten()
            .map { op -> Regex("\\d+").findAll(op).map { it.value.toInt() }.toList() }
    }
}
