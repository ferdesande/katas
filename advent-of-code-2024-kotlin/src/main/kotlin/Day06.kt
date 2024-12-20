package org.fdesande

import org.fdesande.common.Direction
import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point

class Day06 : AdventProblem {

    companion object {
        private const val START = '^'
        private const val WALL = '#'
        private const val INPUT = "/input06.txt"
    }

    override fun firstPart(): String {
        // Valid result: 5242
        val grid = FileUtils.getGrid(INPUT)
        val start = grid.getSingleCharacterPoint(START)
        return getEscapeRoute(grid, Vector(start, Direction.UP))
            .path
            .distinctBy { it.point }
            .size.toString()
    }

    override fun secondPart(): String {
        // Valid result: 1424
        val grid = FileUtils.getGrid(INPUT)
        val start = Vector(grid.getSingleCharacterPoint(START), Direction.UP)
        return getEscapeRoute(grid, start).path.map { it.point }.toSet()
            .filterNot { it == start.point }
            .count { getEscapeRoute(grid, start, it).hasLoop }.toString()
    }

    private fun getEscapeRoute(grid: Grid, start: Vector, obstacle: Point? = null): EscapeRoute {
        var actual = start
        val path = mutableSetOf<Vector>()
        do {
            path.add(actual)
            val ahead = actual.goAhead()
            actual = if (grid.getValue(ahead.point) == WALL || ahead.point == obstacle) actual.turn() else ahead
        } while (grid.isInGrid(actual.point) && actual !in path)

        return EscapeRoute(path, actual in path, obstacle)
    }

    private data class EscapeRoute(val path: Set<Vector>, val hasLoop: Boolean, val obstacle: Point? = null) {
        fun print(grid: Grid): List<String> {
            val bounds = grid.getBounds()
            val list = mutableListOf<String>()
            for (y in bounds.minY until bounds.maxY) {
                val builder = StringBuilder()
                for (x in bounds.minX until bounds.maxX) {
                    val point = Point(x, y)
                    val char = path.filter { it.point == point }.map { it.direction }
                        .let {
                            val vertical = it.contains(Direction.UP) || it.contains(Direction.DOWN)
                            val horizontal = it.contains(Direction.LEFT) || it.contains(Direction.RIGHT)
                            when {
                                obstacle == point -> 'O'
                                vertical && horizontal -> '+'
                                vertical && !horizontal -> '|'
                                !vertical && horizontal -> '-'
                                else -> grid.getValue(point)!!
                            }
                        }
                    builder.append(char)
                }
                list.add(builder.toString())
            }
            return list
        }
    }

    private data class Vector(val point: Point, val direction: Direction) {
        fun goAhead() = when (direction) {
            Direction.UP -> Vector(Point(point.x, point.y - 1), Direction.UP)
            Direction.RIGHT -> Vector(Point(point.x + 1, point.y), Direction.RIGHT)
            Direction.DOWN -> Vector(Point(point.x, point.y + 1), Direction.DOWN)
            Direction.LEFT -> Vector(Point(point.x - 1, point.y), Direction.LEFT)
        }

        fun turn() = when (direction) {
            Direction.UP -> Vector(point, Direction.RIGHT)
            Direction.RIGHT -> Vector(point, Direction.DOWN)
            Direction.DOWN -> Vector(point, Direction.LEFT)
            Direction.LEFT -> Vector(point, Direction.UP)
        }
    }
}
