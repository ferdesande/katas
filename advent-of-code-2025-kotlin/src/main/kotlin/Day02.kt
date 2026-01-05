package com.fsg

import common.FileUtils

fun main() {
    val inputs = readFile("/input02.txt")

    println("part 1: ${part1(inputs)}")
    println("part 2: ${part2(inputs)}")
}

private fun part1(inputs: List<LongRange>): Long {
    val invalidIds = mutableListOf<Long>()
    for (input in inputs) {
        for (id in input) {
            val strId = id.toString()
            if (strId.length % 2 == 0) {
                val length = strId.length / 2
                if (strId.substring(0, length) == strId.substring(length)) {
                    invalidIds.add(id)
                }
            }
        }
    }
    return invalidIds.sum()
}

private fun part2(inputs: List<LongRange>): Long {
    val invalidIds = mutableListOf<Long>()
    for (input in inputs) {
        for (id in input) {
            val strId = id.toString()
            for (length in 1 until strId.length) {
                if (strId.length % length == 0) {
                    val parts = strId.chunked(length)
                    if (parts.all { part -> part == parts.first() }) {
                        invalidIds.add(id)
                        break
                    }
                }
            }
        }
    }
    return invalidIds.sum()
}

private fun readFile(fileName: String): List<LongRange> {
    return FileUtils.getLines(fileName)
        .filter { line -> line.isNotBlank() }
        .flatMap { line ->
            line.split(",")
                .map { it.split("-") }
                .map { parts -> LongRange(parts[0].toLong(), parts[1].toLong()) }
        }
}
