package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point

class Day08 : AdventProblem {

    companion object {
        private const val INPUT = "/input08.txt"
    }

    override fun firstPart(): String {
        // Valid result: 289
        val grid = FileUtils.getGrid(INPUT)
        return getAntiNodes(grid).size.toString()
    }

    override fun secondPart(): String {
        // Valid result: 1030
        val grid = FileUtils.getGrid(INPUT)
        return getAntiNodesMultiple(grid).size.toString()
    }

    private fun getAntiNodes(grid: Grid): Set<Point> =
        extractPairs(grid)
            .flatMap { pair -> createAntiNodes(pair, grid) }
            .toSet()

    private fun getAntiNodesMultiple(grid: Grid): Set<Point> =
        extractPairs(grid)
            .flatMap { pair -> createAntiNodesMultiple(pair, grid) }
            .toSet()

    private fun extractPairs(grid: Grid): List<Pair<Point, Point>> =
        grid.getContent()
            .filter { it.second != '.' }
            .groupBy({ it.second }, { it.first }).values
            .flatMap { points -> getPairs(points) }

    private fun getPairs(points: List<Point>): List<Pair<Point, Point>> {
        val pairs = mutableListOf<Pair<Point, Point>>()
        for (i in 0 until points.lastIndex) {
            for (j in (i + 1)..points.lastIndex) {
                pairs.add(Pair(points[i], points[j]))
            }
        }
        return pairs
    }

    private fun createAntiNodes(pair: Pair<Point, Point>, grid: Grid): List<Point> {
        val distance = pair.second - pair.first

        val point1 = pair.first - distance
        val point2 = pair.second + distance

        return listOf(point1, point2).filter { grid.isInGrid(it) }
    }

    private fun createAntiNodesMultiple(pair: Pair<Point, Point>, grid: Grid): List<Point> {
        val distance = pair.second - pair.first
        val points = mutableListOf(pair.first, pair.second)

        var next = pair.first - distance
        while (grid.isInGrid(next)) {
            points.add(next)
            next -= distance
        }

        next = pair.second + distance
        while (grid.isInGrid(next)) {
            points.add(next)
            next += distance
        }

        return points
    }
}
