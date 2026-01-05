package com.fsg.common

class Grid(lines: List<String>) {
    companion object {
        fun create(rows: Int, cols: Int, defaultChar: Char = '.'): Grid {
            val line = String((0 until cols).map { defaultChar }.toCharArray())
            return Grid((0 until rows).map { line })
        }
    }

    private val grid: MutableMap<Point, Char> = lines
        .mapIndexed { y, value -> value.mapIndexed { x, c -> Point(x, y) to c } }
        .flatten().toMap().toMutableMap()

    fun getSingleCharacterPoint(char: Char): Point = grid.entries.single { it.value == char }.key
    fun isInGrid(point: Point): Boolean = grid.containsKey(point)
    fun getValue(point: Point): Char? = grid[point]
    fun setValue(point: Point, value: Char) {
        if (isInGrid(point)) {
            grid[point] = value
        }
    }

    fun getBounds(): GridBounds {
        return GridBounds(
            minX = grid.keys.minOf { it.x },
            maxX = grid.keys.maxOf { it.x },
            minY = grid.keys.minOf { it.y },
            maxY = grid.keys.maxOf { it.y },
        )
    }

    fun getContent(): List<Pair<Point, Char>> = grid.entries.map { Pair(it.key, it.value) }
    fun print(): String {
        val bounds = getBounds()
        return (bounds.minY..bounds.maxY).joinToString("\n") { y ->
            (bounds.minX..bounds.maxX).map { x -> grid[Point(x, y)] }.joinToString("")
        }
    }

}

data class GridBounds(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int)

data class Point(val x: Int, val y: Int) {
    companion object {
        val UP = Point(x = 0, y = -1)
        val RIGHT = Point(x = 1, y = 0)
        val DOWN = Point(x = 0, y = 1)
        val LEFT = Point(x = -1, y = 0)
    }

    operator fun plus(point: Point): Point = Point(x + point.x, y + point.y)
    operator fun minus(point: Point): Point = Point(x - point.x, y - point.y)
    fun getNeighbors(): List<Point> = listOf(this + UP, this + RIGHT, this + DOWN, this + LEFT)
    fun getAllNeighbors(): List<Point> = listOf(this + UP + LEFT, this + UP, this + UP + RIGHT, this + LEFT,
        this + RIGHT, this + DOWN + LEFT, this + DOWN, this + DOWN + RIGHT
    )
}

data class DirectedPoint(val point: Point, val direction: Direction)

enum class Direction { UP, RIGHT, DOWN, LEFT }
