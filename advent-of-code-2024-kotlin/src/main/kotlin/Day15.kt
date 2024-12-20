package org.fdesande

import org.fdesande.common.Direction
import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point

class Day15 : AdventProblem {

    companion object {
        private const val INPUT = "/input15.txt"

        private const val ROBOT = '@'
        private const val BOX = 'O'
        private const val HALF_BOX_START = '['
        private const val HALF_BOX_END = ']'
        private const val WALL = '#'
        private const val EMPTY = '.'

        private val HORIZONTAL = setOf(Direction.LEFT, Direction.RIGHT)

        private val MOVEMENT = mapOf(
            Direction.UP to Point(x = 0, y = -1),
            Direction.RIGHT to Point(x = 1, y = 0),
            Direction.DOWN to Point(x = 0, y = 1),
            Direction.LEFT to Point(x = -1, y = 0),
        )
    }

    override fun firstPart(): String {
        // Valid result: 1559280
        val input = getInput()
        return solve(input.first, input.second)
            .getContent()
            .filter { it.second == BOX }
            .sumOf { it.first.y * 100 + it.first.x }.toString()
    }

    override fun secondPart(): String {
        // Valid result: 1576353
        val input = getExpandedInput()
        return solve(input.first, input.second)
            .getContent()
            .filter { it.second == HALF_BOX_START }
            .sumOf { it.first.y * 100 + it.first.x }.toString()
    }

    private fun solve(grid: Grid, directions: List<Direction>): Grid {
        var robot = grid.getSingleCharacterPoint(ROBOT)
        for (direction in directions) {
            robot = move(grid, robot, direction)
        }
        return grid
    }

    private fun move(grid: Grid, point: Point, direction: Direction): Point {
        return if (canMove(grid, point, direction)) {
            push(grid, point, direction)
            point + MOVEMENT[direction]!!
        } else {
            point
        }
    }

    private fun canMove(grid: Grid, point: Point, direction: Direction): Boolean {
        val shift = MOVEMENT[direction]!!
        val next = point + shift

        return when (grid.getValue(next)) {
            WALL -> false
            BOX -> canMove(grid, next, direction)
            HALF_BOX_START ->
                if (direction in HORIZONTAL)
                    canMove(grid, next, direction)
                else
                    canMove(grid, next, direction) && canMove(grid, next + Point(1, 0), direction)

            HALF_BOX_END ->
                if (direction in HORIZONTAL)
                    canMove(grid, next, direction)
                else
                    canMove(grid, next, direction) && canMove(grid, next + Point(-1, 0), direction)

            else -> true
        }
    }

    private fun push(grid: Grid, point: Point, direction: Direction) {
        val shift = MOVEMENT[direction]!!
        val next = point + shift

        when (grid.getValue(next)) {
            BOX -> push(grid, next, direction)
            HALF_BOX_START ->
                if (direction in HORIZONTAL)
                    push(grid, next, direction)
                else {
                    push(grid, next, direction)
                    push(grid, next + Point(1, 0), direction)
                }

            HALF_BOX_END ->
                if (direction in HORIZONTAL)
                    push(grid, next, direction)
                else {
                    push(grid, next, direction)
                    push(grid, next + Point(-1, 0), direction)
                }
        }

        push(grid, point, next)
    }

    private fun push(grid: Grid, point: Point, destination: Point) {
        grid.setValue(destination, grid.getValue(point)!!)
        grid.setValue(point, EMPTY)
    }

    private fun getInput(): Pair<Grid, List<Direction>> {
        val lines = FileUtils.getLines(INPUT)
        val separator = lines.indexOfFirst { it.isBlank() }
        return getInput(lines.subList(0, separator), lines.subList(separator + 1, lines.size))
    }

    private fun getExpandedInput(): Pair<Grid, List<Direction>> {
        val lines = FileUtils.getLines(INPUT)
        val separator = lines.indexOfFirst { it.isBlank() }
        val gridLines = lines.subList(0, separator)
            .map { line ->
                line.map {
                    when (it) {
                        WALL -> "$WALL$WALL"
                        BOX -> "$HALF_BOX_START$HALF_BOX_END"
                        EMPTY -> "$EMPTY$EMPTY"
                        ROBOT -> "$ROBOT$EMPTY"
                        else -> null
                    }
                }.joinToString("")
            }
        return getInput(gridLines, lines.subList(separator + 1, lines.size))
    }

    private fun getInput(gridLines: List<String>, movementLines: List<String>): Pair<Grid, List<Direction>> {
        return Pair(Grid(gridLines), movementLines.joinToString("")
            .mapNotNull {
                when (it) {
                    '^' -> Direction.UP
                    '>' -> Direction.RIGHT
                    'v' -> Direction.DOWN
                    '<' -> Direction.LEFT
                    else -> null
                }
            }
        )
    }
}
