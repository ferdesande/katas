package org.fdesande

import org.fdesande.common.FileUtils

class Day22 : AdventProblem {

    companion object {
        private const val INPUT = "/input22.txt"
        private const val PRUNE_VALUE = 16777216L
    }

    override fun firstPart(): String {
        // Valid result: 16619522798
        return FileUtils.getLines(INPUT).filter { it.isNotBlank() }
            .map { it.toLong() }.sumOf {
                var result = it
                repeat(2000) { result = evolve(result) }
                result
            }.toString()
    }

    override fun secondPart(): String {
        // Valid result: 1854
        return FileUtils.getLines(INPUT).filter { it.isNotBlank() }
            .map { it.toLong() }
            .map { value ->
                val evolution = mutableListOf(value)
                var next = value
                repeat(1999) {
                    next = evolve(next)
                    evolution.add(next)
                }
                evolution
                    .map { (it % 10).toInt() }
                    .zipWithNext { a, b -> b to (b - a) }
            }.map { sellPointByEvolution(it) }
            .flatMap { it.map { entry -> entry.key to entry.value } }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.sum() }
            .maxBy { it.value }.value.toString()
    }

    private fun mix(first: Long, second: Long): Long = first xor second
    private fun prune(value: Long): Long = value % PRUNE_VALUE

    private fun evolve(secretNumber: Long): Long {
        val firstStep = prune(mix(secretNumber * 64, secretNumber))
        val secondStep = prune(mix(firstStep / 32, firstStep))
        return prune(mix(secondStep * 2048, secondStep))
    }

    private fun sellPointByEvolution(evolution: List<Pair<Int, Int>>): Map<List<Int>, Int> {
        val bananasByEvolution = mutableMapOf<List<Int>, Int>()
        for (i in 0..evolution.lastIndex - 3) {
            val key = (i..i + 3).map { evolution[it].second }
            if (key !in bananasByEvolution.keys) {
                bananasByEvolution[key] = evolution[i + 3].first
            }
        }

        return bananasByEvolution
    }
}
