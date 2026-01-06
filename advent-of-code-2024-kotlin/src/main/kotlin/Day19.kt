package org.fdesande

import org.fdesande.common.FileUtils

class Day19 : AdventProblem {

    companion object {
        private const val INPUT = "/input19.txt"
    }

    override fun firstPart(): String {
        return getOnsenResult().count { it.isNotEmpty() }.toString()
    }

    override fun secondPart(): String {
        return getOnsenResult().sumOf { it.sumOf { solution -> solution.solutions } }.toString()
    }

    private fun getOnsenResult(): List<List<OnsenResult>> {
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val patterns = lines.first().split(',').map(String::trim)
        return lines.subList(1, lines.size).map { onsen -> getOnsenPattern(patterns, onsen) }
    }

    private fun getOnsenPattern(patterns: List<String>, onsen: String): List<OnsenResult> {
        var next: List<Pair<String, OnsenResult>> = listOf(Pair(onsen, OnsenResult(emptyList(), 1L)))
        val results = mutableListOf<OnsenResult>()
        while (next.isNotEmpty()) {
            val preGrouped = next
                .filter { it.first.isNotEmpty() }
                .flatMap { pair ->
                    patterns.filter { pattern -> pair.first.indexOf(pattern) == 0 }
                        .map { match ->
                            Pair(
                                pair.first.substring(match.length),
                                OnsenResult(pair.second.sample + match, pair.second.solutions)
                            )
                        }
                }
            val grouped = preGrouped.groupBy({ it.first }, { it.second })
            next = grouped.map {
                val sampleSolution = it.value.first().sample
                val solutions = it.value.sumOf { onsenSolutions -> onsenSolutions.solutions }
                Pair(it.key, OnsenResult(sampleSolution, solutions))
            }
            next.filter { it.first.isEmpty() }.forEach { results += it.second }
        }

        return results
    }

    private data class OnsenResult(val sample: List<String>, val solutions: Long)
}
