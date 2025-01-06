package org.fdesande

import org.fdesande.common.FileUtils

class Day02 : AdventProblem {

    companion object {
        private const val INPUT = "/input02.txt"
    }

    override fun firstPart(): String {
        // Valid result: 1606483
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        return extractDimensions(lines)
            .map { it + it[0] }
            .map { it.zipWithNext { a, b -> a * b } }
            .sumOf { 2 * it.sum() + it.min() }
            .toString()
    }

    override fun secondPart(): String {
        // Valid result: 3842356
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val dimensions = extractDimensions(lines)

        val minPerimeter = dimensions.map { it.sorted().dropLast(1) }.map { 2 * it.sum() }
        val bowSizes = dimensions.map { it.reduce { a, b -> a * b } }
        return (minPerimeter.sum() + bowSizes.sum()).toString()
    }

    private fun extractDimensions(lines: List<String>): Sequence<List<Int>> = lines
        .asSequence()
        .map { line -> line.split("x").map { it.toInt() } }
}
