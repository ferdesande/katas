package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point

class Day18 : AdventProblem {

    companion object {
        private const val INPUT = "/input18.txt"
        private const val ON = '#'
        private const val OFF = '.'
    }

    override fun firstPart(): String {
        val grid = FileUtils.getGrid(INPUT)
        return evolveGrid(grid, false)
    }

    override fun secondPart(): String {
        val grid = FileUtils.getGrid(INPUT)
        return evolveGrid(grid, true)
    }

    private fun evolveGrid(grid: Grid<Char>, turnOnCorners: Boolean): String {
        repeat(100) {
            if (turnOnCorners) grid.turnOnCorners()

            grid.getContent().map { pair ->
                val turnOnNeighbors = pair.first.getFullNeighbors()
                    .filter { grid.isInGrid(it) }
                    .count { grid.getValue(it) == ON }
                pair.first to when (pair.second) {
                    ON -> if (turnOnNeighbors in (2..3)) ON else OFF
                    OFF -> if (turnOnNeighbors == 3) ON else OFF
                    else -> throw IllegalArgumentException("something went wrong")
                }
            }.forEach { pair -> grid.setValue(pair.first, pair.second) }
        }

        if (turnOnCorners) grid.turnOnCorners()

        return grid.getContent().count { it.second == ON }.toString()
    }

    private fun Grid<Char>.turnOnCorners() {
        val bounds = getBounds()
        setValue(Point(bounds.minX, bounds.minY), ON)
        setValue(Point(bounds.minX, bounds.maxY), ON)
        setValue(Point(bounds.maxX, bounds.minY), ON)
        setValue(Point(bounds.maxX, bounds.maxY), ON)
    }

    private fun Point.getFullNeighbors() =
        listOf(
            this + Point(-1, -1),
            this + Point(-1, 0),
            this + Point(-1, 1),
            this + Point(0, -1),
            this + Point(0, 1),
            this + Point(1, -1),
            this + Point(1, 0),
            this + Point(1, 1),
        )
}
