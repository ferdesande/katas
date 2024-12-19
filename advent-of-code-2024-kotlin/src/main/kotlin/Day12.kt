package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point

class Day12 : AdventProblem {

    companion object {
        private const val INPUT = "/input12.txt"
    }

    override fun firstPart(): String {
        // Valid result: 1359028
        val grid = FileUtils.getGrid(INPUT)
        return getGroups(grid).sumOf { getFenceBorderPoints(it).size * it.size }.toString()
    }

    // Valid result:
    override fun secondPart(): String {
        // Valid result: 839780
        val grid = FileUtils.getGrid(INPUT)
        return getGroups(grid).sumOf { getFenceBorderCount(it) * it.size }.toString()
    }

    private fun getFenceBorderPoints(crop: Set<Point>): List<DirectedPoint> =
        crop.flatMap { point -> point.getDirectedNeighbours().filterNot { it.point in crop } }

    private fun getFenceBorderCount(crop: Set<Point>): Int {
        val borderPoints = getFenceBorderPoints(crop)

        return getSidesByDirection(borderPoints, Direction.UP) +
            getSidesByDirection(borderPoints, Direction.RIGHT) +
            getSidesByDirection(borderPoints, Direction.DOWN) +
            getSidesByDirection(borderPoints, Direction.LEFT)
    }

    private fun getSidesByDirection(borderPoints: List<DirectedPoint>, direction: Direction): Int {
        val filtered = borderPoints.filter { it.direction == direction }
        return when (direction) {
            Direction.UP, Direction.DOWN -> filtered.groupBy({ it.point.y }, { it.point.x })
            Direction.RIGHT, Direction.LEFT -> filtered.groupBy({ it.point.x }, { it.point.y })
        }.values.sumOf { getCropSides(it) }
    }

    private fun getGroups(grid: Grid): List<Set<Point>> =
        grid.getContent()
            .groupBy({ it.second }, { it.first })
            .values.flatMap { points -> splitCrops(points.toSet()) }

    private fun splitCrops(points: Set<Point>): List<Set<Point>> {
        val result = mutableListOf<Set<Point>>()
        val visited = mutableSetOf<Point>()

        for (point in points) {
            if (point !in visited) {
                val crop = getCropBorder(points, point)
                visited += crop
                result.add(crop)
            }
        }

        return result
    }

    private fun getCropBorder(crop: Set<Point>, start: Point): Set<Point> {
        val border = mutableSetOf(start)
        var neighbours = setOf(start)
        while (neighbours.isNotEmpty()) {
            neighbours = neighbours
                .flatMap { point -> point.getNeighbours() }
                .filter { point -> point in crop }
                .filterNot { point -> border.contains(point) }
                .toSet()

            border += neighbours
        }

        return border
    }

    private fun getCropSides(points: List<Int>): Int {
        var sides = 1
        points.sorted().zipWithNext { a, b -> if (b - a != 1) sides++ }
        return sides
    }

    private fun Point.getDirectedNeighbours(): List<DirectedPoint> =
        listOf(
            DirectedPoint(Point(x, y - 1), Direction.UP),
            DirectedPoint(Point(x + 1, y), Direction.RIGHT),
            DirectedPoint(Point(x, y + 1), Direction.DOWN),
            DirectedPoint(Point(x - 1, y), Direction.LEFT),
        )

    private enum class Direction { UP, RIGHT, DOWN, LEFT }
    private data class DirectedPoint(val point: Point, val direction: Direction)
}
