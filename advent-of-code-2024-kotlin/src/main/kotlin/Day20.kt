package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point
import kotlin.math.abs
import kotlin.streams.asSequence

class Day20 : AdventProblem {

    companion object {
        private const val INPUT = "/input20.txt"

        private const val START = 'S'
        private const val END = 'E'
        private const val WALL = '#'
    }

    override fun firstPart(): String {
        // Valid result: 1502
        val grid = FileUtils.getGrid(INPUT)
        return calculateCheatsByPoint(grid, 2).values.count { it >= 100 }.toString()
    }

    override fun secondPart(): String {
        // Valid result:
        val grid = FileUtils.getGrid(INPUT)
        return calculateCheatsByPoint(grid, 20).entries.count { it.value >= 100 }.toString()
    }

    private fun calculateCheatsByPoint(grid: Grid, cheatDistance: Int): Map<Pair<Point, Point>, Int> {
        val result = getNormalResult(grid)
        val resultIndexByPoint = result.mapIndexed { index, point -> point to index }.toMap()
        val matrix = getPointMatrix(cheatDistance)

        return result
            .parallelStream()
            .flatMap { point -> matrix.parallelStream().map { Pair(point, it + point) } }
            .filter { it.second in result }
            .asSequence().toSet()
            .parallelStream()
            .map {
                val distance =
                    resultIndexByPoint[it.second]!! - resultIndexByPoint[it.first]!! - it.first.getDistance(it.second)
                Pair(it.first, it.second) to distance
            }
            .asSequence()
            .associate { it.first to it.second }
    }

    private fun getNormalResult(grid: Grid): List<Point> {
        val start = grid.getSingleCharacterPoint(START)
        val end = grid.getSingleCharacterPoint(END)
        val visited = mutableListOf(start)
        var current = start
        while (current != end) {
            current = current.getNeighbors()
                .filterNot { grid.getValue(it) == WALL }
                .filterNot { it in visited }
                .single()

            visited += current
        }

        return visited
    }

    private fun Point.getDistance(point: Point) = abs(this.x - point.x) + abs(this.y - point.y)

    private fun getPointMatrix(distance: Int): Set<Point> =
        (-distance..distance).asSequence().map { x ->
            (-distance..distance).map { y -> Point(x, y) }
        }.flatten()
            .filter { (abs(it.x) + abs(it.y)) <= distance }
            .filterNot { it == Point(0, 0) }
            .toSet()
}
