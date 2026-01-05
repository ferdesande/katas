package com.fsg

import common.FileUtils
import kotlin.math.max
import kotlin.math.min

fun main() {
    val input = readFile("/input05.txt")

    println("part 1: ${part1(input.first, input.second)}")
    println("part 2: ${part2(input.first, input.second)}")
}

private fun part1(freshIdRanges: List<LongRange>, ingredientsId: List<Long>): Int =
    ingredientsId.count { id -> freshIdRanges.any { range -> id in range } }

private fun part2(freshIdRanges: List<LongRange>, ingredientsId: List<Long>): Long =
    freshIdRanges.sumOf { range -> range.endInclusive - range.start + 1 }

private fun readFile(fileName: String): Pair<List<LongRange>, List<Long>> {
    val lines = FileUtils.getLines(fileName)
    val splitIndex = lines.indexOfFirst { line -> line.isBlank() }
    val databaseIndexes = lines.subList(0, splitIndex)
        .map { line -> line.split("-") }
        .map { parts -> LongRange(parts[0].toLong(), parts[1].toLong()) }

    val ingredientIds = lines.subList(splitIndex, lines.size)
        .filter { it.isNotBlank() }
        .map { it.toLong() }

    return mergeRanges(databaseIndexes) to ingredientIds
}

private fun mergeRanges(databaseIndexes: List<LongRange>): List<LongRange> {
    var ranges = listOf<LongRange>()
    databaseIndexes.forEach { range -> ranges = mergeRanges(range, ranges) }
    return ranges
}

private fun mergeRanges(rangeToAdd: LongRange, ranges: List<LongRange>): List<LongRange> {
    val mergedRanges = mutableListOf<LongRange>()
    var currentRange = rangeToAdd
    ranges.forEach { range ->
        if (range.endInclusive < currentRange.start || range.start > currentRange.endInclusive) {
            mergedRanges.add(range)
        } else {
            val start = min(currentRange.start, range.start)
            val end = max(currentRange.endInclusive, range.endInclusive)
            currentRange = LongRange(start, end)
        }
    }
    mergedRanges.add(currentRange)
    return mergedRanges
}
