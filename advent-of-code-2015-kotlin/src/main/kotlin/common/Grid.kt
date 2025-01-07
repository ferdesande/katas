package org.fdesande.common

class Grid<T>(values: Map<Point, T>) {
    companion object {
        fun <T> create(rows: Int, cols: Int, defaultValue: T): Grid<T> {
            val values = (0 until rows)
                .flatMap { x -> (0 until cols).map { y -> Point(x, y) to defaultValue } }
                .toMap()
            return Grid(values)
        }

        fun create(lines: List<String>): Grid<Char> {
            val values = lines
                .mapIndexed { y, value -> value.mapIndexed { x, c -> Point(x, y) to c } }
                .flatten().toMap()
            return Grid(values)
        }
    }

    private val grid: MutableMap<Point, T> = values.toMutableMap()

    fun isInGrid(point: Point): Boolean = grid.containsKey(point)
    fun getValue(point: Point): T? = grid[point]
    fun setValue(point: Point, value: T) {
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

    fun getContent(): List<Pair<Point, T>> = grid.entries.map { Pair(it.key, it.value) }
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
}
