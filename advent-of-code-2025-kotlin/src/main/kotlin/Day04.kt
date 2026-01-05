package com.fsg

import com.fsg.common.Grid
import com.fsg.common.Point
import common.FileUtils

private const val ROLL_OF_PAPER = '@'
private const val EMPTY_SPACE = '.'

fun main() {
    val grid = FileUtils.getGrid("/input04.txt")

    println("part 1: ${part1(grid)}")
    println("part 2: ${part2(grid)}")
}

private fun part1(grid: Grid): Int = getRemovableRolls(grid).count()

private fun part2(grid: Grid): Int {
    var removedRollCount = 0
    var rollsToRemove = getRemovableRolls(grid)
    while (rollsToRemove.isNotEmpty()) {
        removedRollCount += getRemovableRolls(grid).count()
        rollsToRemove.forEach { point -> grid.setValue(point, EMPTY_SPACE) }
        rollsToRemove = getRemovableRolls(grid)
    }

    return removedRollCount
}

private fun getRemovableRolls(grid: Grid): List<Point> = grid.getContent()
    .filter { it.second == ROLL_OF_PAPER }
    .filter { cell ->
        val neighborsWithRollPaper = cell.first
            .getAllNeighbors()
            .mapNotNull { grid.getValue(it) }
            .count { it == ROLL_OF_PAPER }
        neighborsWithRollPaper < 4
    }.map { it.first }
