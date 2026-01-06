package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point

class Day18 : AdventProblem {

    companion object {
        private const val INPUT = "/input18.txt"
    }

    override fun firstPart(): String {
        val grid = Grid.create(71, 71)
        val noise = extractNoise().take(1024).toSet()

        return (findShortestPath(grid, noise).size - 1).toString()
    }

    override fun secondPart(): String {
        val grid = Grid.create(71, 71)
        val noise = extractNoise()
        var maxValid = 1024
        var minInvalid = noise.size - 1
        while ((minInvalid - maxValid) > 1) {
            val bit = maxValid + (minInvalid - maxValid) / 2
            if (findShortestPath(grid, noise.take(bit).toSet()).isEmpty()) {
                minInvalid = bit
            } else {
                maxValid = bit
            }
        }
        return "${noise[maxValid].x},${noise[maxValid].y}"
    }

    private fun findShortestPath(grid: Grid, noise: Set<Point>): Set<Point> {
        val bounds = grid.getBounds()
        val start = Point(bounds.minX, bounds.minY)
        val end = Point(bounds.maxX, bounds.maxY)

        val visited = mutableSetOf(start)
        var paths = setOf(setOf(start))
        while (paths.isNotEmpty()) {
            val pathByPoint = paths.flatMap { path ->
                path.last()
                    .getNeighbors()
                    .filterNot { visited.contains(it) }
                    .filter { grid.isInGrid(it) }
                    .filterNot { it in noise }
                    .map { path + it }
            }.groupBy({ it.last() }) { it }.mapValues { it.value.first() }
            pathByPoint.values
                .firstOrNull { it.last() == end }
                ?.let { return it }

            visited += pathByPoint.map { it.key }
            paths = pathByPoint.values.toSet()
        }

        return emptySet()
    }

    private fun extractNoise(): List<Point> {
        return FileUtils.getLines(INPUT).filter { it.isNotBlank() }
            .map { it.split(",").map(String::toInt) }
            .map { Point(it[0], it[1]) }
    }
}
