package org.fdesande

import org.fdesande.common.FileUtils

class Day03 : AdventProblem {

    companion object {
        private const val INPUT = "/input03.txt"
        private const val DISABLE_INS = "don't()"
        private const val ENABLE_INS = "do()"
    }

    override fun firstPart(): String {
        return extractMultipliers(
            readFile(INPUT)
        ).sumOf { multipliers -> multipliers[0] * multipliers[1] }.toString()
    }

    override fun secondPart(): String {
        // Result Too high (see what happens)
        val text = readFile(INPUT)
            .joinToString("\\n")

        var currentIndex = 0
        var nextIndex = text.indexOf(DISABLE_INS)
        var enabled = true
        val sb = StringBuilder()
        while (nextIndex != -1) {
            if (enabled) {
                enabled = false
                sb.append(text.substring(currentIndex, nextIndex))
                currentIndex = nextIndex
                nextIndex = text.indexOf(ENABLE_INS, currentIndex)
            } else {
                enabled = true
                currentIndex = nextIndex
                nextIndex = text.indexOf(DISABLE_INS, currentIndex)
            }
        }

        if (enabled) {
            sb.append(text.substring(currentIndex))
        }

        return extractMultipliers(listOf(sb.toString()))
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
