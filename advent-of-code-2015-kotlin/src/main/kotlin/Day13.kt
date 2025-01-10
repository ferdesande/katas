package org.fdesande

import org.fdesande.common.FileUtils

class Day13 : AdventProblem {

    companion object {
        private const val INPUT = "/input13.txt"
        private val REGEX = Regex("""(\w+) would (\w+) (\d+) happiness units by sitting next to (\w+).""")
    }

    private val combinationFinder = CombinationFinder()

    override fun firstPart(): String {
        // Valid result: 709
        val grid = FileUtils.getLines(INPUT)
            .mapNotNull { parseLine(it) }
            .groupBy({ it.first.first }, { it.first.second to it.second })
            .mapValues { it.value.toMap() }

        return calculateMaxAffinity(grid)
    }

    override fun secondPart(): String {
        // Valid result: 668
        val grid = FileUtils.getLines(INPUT)
            .mapNotNull { parseLine(it) }
            .groupBy({ it.first.first }, { it.first.second to it.second })
            .mapValues { it.value.union(listOf("me" to 0)) }
            .mapValues { it.value.toMap() }
            .toMutableMap()

        grid["me"] = grid.keys.associateWith { 0 }

        return calculateMaxAffinity(grid)
    }

    private fun calculateMaxAffinity(grid: Map<String, Map<String, Int>>): String {
        val keys = grid.keys.toList()
        val pairs = getPairs(grid.keys.toList())
        val valid = combinationFinder.getValidCombinations(pairs, keys.size)
        return valid.maxOf { combination ->
            combination.sumOf { pair -> grid[pair.first]!![pair.second]!! + grid[pair.second]!![pair.first]!! }
        }.toString()
    }

    private fun getPairs(persons: List<String>): Set<Pair<String, String>> =
        (0..<persons.lastIndex).flatMap { i ->
            (i + 1..persons.lastIndex).map { j -> Pair(persons[i], persons[j]) }
        }.toSet()

    private fun parseLine(line: String): Pair<Pair<String, String>, Int>? =
        REGEX.find(line)?.destructured?.let { (from, verb, amount, to) ->
            val sign = if (verb == "gain") 1 else -1
            Pair(Pair(from, to), sign * amount.toInt())
        }

    private class CombinationFinder {
        fun getValidCombinations(combinations: Set<Pair<String, String>>, n: Int): List<Set<Pair<String, String>>> {
            if (n == 0) return emptyList()

            val valid = mutableListOf<Set<Pair<String, String>>>()
            val visited = mutableSetOf<Pair<String, String>>()
            for (combination in combinations) {
                visited += combination
                complete(setOf(combination), combinations.filterNot { it in visited }.toSet(), valid, n - 2)
            }
            return valid
        }

        private fun complete(
            current: Set<Pair<String, String>>,
            combinations: Set<Pair<String, String>>,
            valid: MutableList<Set<Pair<String, String>>>,
            n: Int
        ) {
            val visited = mutableSetOf<Pair<String, String>>()
            combinations.forEach { next ->
                visited += next
                val candidate = current + next
                if (n == 0) {
                    valid += candidate
                } else {
                    val remainingCombinations = getValidRemaining(candidate, combinations - next)
                        .filterNot { it in visited }.toSet()
                    if (remainingCombinations.isNotEmpty()) {
                        complete(candidate, remainingCombinations, valid, n - 1)
                    }
                }
            }
        }

        private fun getValidRemaining(
            candidate: Set<Pair<String, String>>,
            combinations: Set<Pair<String, String>>
        ): Set<Pair<String, String>> {
            val countByValue = candidate.flatMap { listOf(it.first, it.second) }.groupingBy { it }.eachCount()
            val completed = countByValue.filter { it.value == 2 }.keys
            return combinations
                .filterNot { it.first in completed || it.second in completed }.toSet()
        }
    }
}
