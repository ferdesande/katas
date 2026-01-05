package com.fsg

import common.FileUtils
import kotlin.math.pow

fun main() {
    val inputs = readFile("/input03.txt")
        .map { input -> input.chunked(1).map { it.toInt() } }

    println("part 1: ${part1(inputs)}")
    println("part 2: ${part2(inputs)}")
}

private fun part1(inputs: List<List<Int>>): Int = inputs.sumOf { input ->
    val firstIndex = getIndexOfMax(input.subList(0, input.size - 1))
    val secondIndex = getIndexOfMax(input.subList(firstIndex + 1, input.size)) + firstIndex + 1

    val joltage = 10 * input[firstIndex] + input[secondIndex]
    joltage
}

private fun part2(inputs: List<List<Int>>): Long = inputs
    .map { input ->
        var missingElements = 12
        var currentIndex = 0
        val digits = mutableListOf<Int>()

        while (missingElements > 0) {
            val subList = input.subList(currentIndex, input.size - missingElements + 1)
            val index = getIndexOfMax(subList)
            digits.add(input[currentIndex + index])
            currentIndex += index + 1
            missingElements--
        }
        digits
    }.sumOf { joltage ->
        joltage
            .reversed()
            .mapIndexed { index, joltage -> 10.0.pow(index.toDouble()) * joltage }
            .sumOf { it.toLong() }
    }

private fun getIndexOfMax(input: List<Int>): Int {
    var maxIndex = 0
    var max = input.first()
    input.forEachIndexed { index, value ->
        if (value > max) {
            maxIndex = index
            max = value
        }
    }

    return maxIndex
}

private fun readFile(fileName: String): List<String> {
    return FileUtils.getLines(fileName)
        .filter(String::isNotBlank)
}
