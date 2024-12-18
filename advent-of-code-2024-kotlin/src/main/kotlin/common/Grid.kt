package org.fdesande.common

class Grid(lines: List<String>) {
    private val grid: Map<Point, Char> = lines
        .mapIndexed { y, value -> value.mapIndexed { x, c -> Point(x, y) to c } }
        .flatten().toMap()

    fun getSingleCharacterPoint(char: Char): Point = grid.entries.single { it.value == char }.key
    fun isInGrid(point: Point): Boolean = grid.containsKey(point)
    fun getValue(point: Point): Char? = grid[point]
    fun getBounds(): GridBounds {
        return GridBounds(
            minX = grid.keys.minOf { it.x },
            maxX = grid.keys.maxOf { it.x },
            minY = grid.keys.minOf { it.y },
            maxY = grid.keys.maxOf { it.y },
        )
    }

    fun getContent(): List<Pair<Point, Char>> = grid.entries.map { Pair(it.key, it.value) }
}

data class GridBounds(val minX: Int, val maxX: Int, val minY: Int, val maxY: Int)

data class Point(val x: Int, val y: Int) {
    operator fun plus(point: Point): Point = Point(x + point.x, y + point.y)
    operator fun minus(point: Point): Point = Point(x - point.x, y - point.y)
    fun getNeighbours(): List<Point> = listOf(Point(x, y - 1), Point(x + 1, y), Point(x, y + 1), Point(x - 1, y))
}
