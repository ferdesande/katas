package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Point

class Day10 : AdventProblem {

    companion object {
        private const val BASE = 0
        private const val TOP = 9
        private const val INPUT = "/input10.txt"
    }

    override fun firstPart(): String {
        // Valid result: 514
        val stringGrid = FileUtils.getGrid(INPUT)
        val grid = stringGrid.getContent().associate { it.first to it.second.digitToInt() }
        return grid.entries
            .filter { it.value == BASE }
            .map { it.key }
            .sumOf { point -> getTops(grid, point).distinct().size }
            .toString()
    }

    override fun secondPart(): String {
        // Valid result: 1162
        val stringGrid = FileUtils.getGrid(INPUT)
        val grid = stringGrid.getContent()
            .associate { it.first to if (it.second == '.') -1 else it.second.digitToInt() }
        return grid.entries
            .filter { it.value == BASE }
            .map { it.key }
            .sumOf { point -> getTops(grid, point).size }
            .toString()
    }

    private fun getTops(grid: Map<Point, Int>, start: Point): List<Point> {
        var neighbors = listOf(start)
        var step = 0
        while (neighbors.isNotEmpty()) {
            if (step == TOP) {
                return neighbors
            }

            step++
            neighbors = neighbors
                .flatMap { point: Point -> point.getNeighbors() }
                .filter { neighbor -> neighbor in grid.keys }
                .filter { neighbor -> grid[neighbor] == step }
        }

        return emptyList()
    }
}
