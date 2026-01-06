package org.fdesande

import org.fdesande.common.FileUtils

class Day11 : AdventProblem {

    companion object {
        private const val INPUT = "/input11.txt"
    }

    override fun firstPart(): String = calculateStonesEvolution(25).toString()

    override fun secondPart(): String = calculateStonesEvolution(75).toString()

    private fun calculateStonesEvolution(blinkCount: Int): Long {
        var stones = FileUtils.getLines(INPUT).first()
            .split(" ")
            .map { it.toLong() }.groupingBy { it }.eachCount()
            .mapValues { it.value.toLong() }

        repeat(blinkCount) { stones = blink(stones) }
        return stones.entries.sumOf { it.value }
    }

    private fun blink(stones: Map<Long, Long>): Map<Long, Long> =
        stones.entries.map { entry -> transform(entry.key).map { it to entry.value } }
            .flatten()
            .fold(mutableMapOf()) { map, entry ->
                map[entry.first] = entry.second + (map[entry.first] ?: 0)
                map
            }

    private fun transform(stone: Long): List<Long> {
        val value = stone.toString()
        return when {
            stone == 0L -> listOf(1L)
            value.length % 2 == 0 -> {
                val half = value.length / 2
                listOf(value.substring(0 until half), value.substring(half)).map { it.toLong() }
            }

            else -> listOf(stone * 2024)
        }
    }
}
