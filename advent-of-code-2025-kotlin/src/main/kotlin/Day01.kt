package com.fsg

import common.FileUtils
import kotlin.math.abs

private const val DIAL_SIZE = 100
private const val STARTING_DIAL = 50

fun main() {
    val inputs = readFile("/input01.txt")

    println("part 1: ${part1(inputs)}")
    println("part 2: ${part2(inputs)}")
}

private fun part1(inputs: List<Input>): Int {
    var current = STARTING_DIAL
    var timesAtZero = 0

    inputs.forEach { input ->
        current = if (input.direction == Direction.Left) {
            current - input.ticks
        } else {
            current + input.ticks
        }

        current = current % DIAL_SIZE
        if (current == 0) {
            timesAtZero++
        }
    }
    return timesAtZero
}

private fun part2(inputs: List<Input>): Int {
    var current = STARTING_DIAL
    var timesAtZero = 0

    inputs.forEach { input ->
        timesAtZero += abs(input.ticks / DIAL_SIZE)
        val remainingTicks = input.ticks % DIAL_SIZE

        current = if (input.direction == Direction.Left) {
            if (current != 0) {
                current - remainingTicks
            } else {
                DIAL_SIZE - remainingTicks
            }
        } else {
            current + remainingTicks
        }

        if (current <= 0 || current >= DIAL_SIZE) {
            timesAtZero++
        }

        current = current % DIAL_SIZE
        if (current < 0) {
            current += DIAL_SIZE
        }

    }
    return timesAtZero
}

private fun readFile(fileName: String): List<Input> {
    return FileUtils.getLines(fileName)
        .filter { line -> line.isNotBlank() }
        .map { line ->
            Input(
                direction = if (line[0] == 'L') Direction.Left else Direction.Right,
                ticks = line.substring(1).toInt()
            )
        }
}

private enum class Direction { Left, Right }
private data class Input(
    val direction: Direction,
    val ticks: Int
)
