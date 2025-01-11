package org.fdesande

import org.fdesande.common.FileUtils

class Day17 : AdventProblem {

    companion object {
        private const val INPUT = "/input17.txt"
        private const val EXPECTED_LITERS = 150
    }

    override fun firstPart(): String {
        // Valid result: 4372
        val containers = FileUtils.getLines(INPUT).filter { it.isNotBlank() }.map { it.toInt() }
        return findCombinations(containers).size.toString()
    }

    override fun secondPart(): String {
        // Valid result: 4
        val containers = FileUtils.getLines(INPUT).filter { it.isNotBlank() }.map { it.toInt() }
        val combinations = findCombinations(containers).map { it.size }.groupingBy { it }.eachCount()
        return combinations[combinations.minOf { it.key }].toString()
    }

    private fun findCombinations(containers: List<Int>): List<List<Int>> {
        val found = mutableListOf<List<Int>>()
        for (i in containers.indices) {
            val pending = containers.subList(i + 1, containers.size)
            findCombinations(pending,  listOf(containers[i]), found)
        }
        return found
    }

    private fun findCombinations(
        containers: List<Int>,
        current: List<Int>,
        found: MutableList<List<Int>>
    ) {
        for (i in containers.indices) {
            val candidate = current + containers[i]
            val actual = candidate.sum()
            if (actual == EXPECTED_LITERS) {
                found += candidate
            } else if (actual < EXPECTED_LITERS) {
                val pending = containers.subList(i + 1, containers.size)
                findCombinations(pending, candidate, found)
            }
        }
    }
}
