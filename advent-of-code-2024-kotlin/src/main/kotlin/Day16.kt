package org.fdesande

import org.fdesande.common.*

class Day16 : AdventProblem {

    companion object {
        private const val INPUT = "/input16.txt"

        private const val START = 'S'
        private const val END = 'E'
        private const val WALL = '#'

        private const val STRAIGHT_SCORE = 1
        private const val TURN_SCORE = 1000
    }

    private var bestPath: Path?

    init {
        val grid = FileUtils.getGrid(INPUT)
        bestPath = getBestPaths(grid)
    }

    override fun firstPart(): String {
        return (bestPath?.score ?: Int.MAX_VALUE).toString()
    }

    override fun secondPart(): String {
        return bestPath?.visited?.size?.toString() ?: "Error"
    }

    private fun getBestPaths(grid: Grid): Path? {
        val start = Path(grid.getSingleCharacterPoint(START))
        val watcher = PathWatcher(grid, start.current)

        var paths = setOf(start)
        var bestScore = Int.MAX_VALUE
        var bestPaths: MutableList<Path> = mutableListOf()

        while (paths.isNotEmpty()) {
            paths = watcher.watch(paths.flatMap { it.move() })

            paths
                .filter { watcher.isEnd(it) }
                .forEach { path ->
                    val score = path.score
                    if (score == bestScore) {
                        bestPaths += path
                    } else if (score < bestScore) {
                        bestPaths = mutableListOf(path)
                        bestScore = score
                    }
                }
        }
        return watcher.merge(bestPaths)
    }

    private class PathWatcher(private val grid: Grid, start: DirectedPoint) {
        private enum class BiDirection { VERTICAL, HORIZONTAL }

        private fun Direction.toBidirectional() = when (this) {
            Direction.UP, Direction.DOWN -> BiDirection.VERTICAL
            Direction.RIGHT, Direction.LEFT -> BiDirection.HORIZONTAL
        }

        private val visited = mutableMapOf(
            Pair(start.point, start.direction.toBidirectional()) to 0
        )

        fun watch(paths: List<Path>): Set<Path> {
            val alivePaths = paths
                .filter { isInGrid(it) }
                .filterNot { isInWall(it) }
                .filter { shouldVisit(it) }

            updateVisited(alivePaths)
            return mergeByPoint(alivePaths).toSet()
        }

        fun isEnd(path: Path): Boolean = grid.getValue(path.current.point) == END
        fun merge(paths: List<Path>): Path? =
            when (paths.size) {
                0 -> null
                1 -> paths.single()
                else -> {
                    val minScore = paths.minOfOrNull { it.score }
                    val joinedPoints = paths.filter { it.score == minScore }.flatMap { it.visited }.toSet()
                    val first = paths.first()
                    Path(first.current, joinedPoints, first.score)
                }
            }

        private fun isInGrid(path: Path): Boolean = grid.isInGrid(path.current.point)
        private fun isInWall(path: Path): Boolean = grid.getValue(path.current.point) == WALL
        private fun shouldVisit(path: Path): Boolean {
            val currentScore = visited[Pair(path.current.point, path.current.direction.toBidirectional())]
            return currentScore == null || currentScore > path.score
        }

        private fun mergeByPoint(paths: List<Path>): List<Path> =
            paths.groupBy({ it.current }, { it })
                .mapNotNull { entry -> merge(entry.value) }

        private fun updateVisited(alivePaths: List<Path>) {
            alivePaths
                .map { Pair(it.current.point, it.current.direction.toBidirectional()) to it.score }
                .forEach {
                    val currentScore = visited[it.first] ?: Int.MAX_VALUE
                    if (currentScore > it.second) {
                        visited[it.first] = it.second
                    }
                }
        }
    }

    private data class Path(val current: DirectedPoint, val visited: Set<Point>, val score: Int) {
        constructor(point: Point) : this(DirectedPoint(point, Direction.RIGHT), setOf(point), 0)

        fun move(): List<Path> =
            getNeighbors()
                .map { neighbor -> Path(neighbor.first, visited + neighbor.first.point, score + neighbor.second) }

        private fun getNeighbors(): List<Pair<DirectedPoint, Int>> =
            when (current.direction) {
                Direction.UP -> listOf(
                    Pair(DirectedPoint(current.point + Point.UP, Direction.UP), STRAIGHT_SCORE),
                    Pair(DirectedPoint(current.point, Direction.RIGHT), TURN_SCORE),
                    Pair(DirectedPoint(current.point, Direction.LEFT), TURN_SCORE),
                )

                Direction.RIGHT -> listOf(
                    Pair(DirectedPoint(current.point + Point.RIGHT, Direction.RIGHT), STRAIGHT_SCORE),
                    Pair(DirectedPoint(current.point, Direction.DOWN), TURN_SCORE),
                    Pair(DirectedPoint(current.point, Direction.UP), TURN_SCORE),
                )

                Direction.DOWN -> listOf(
                    Pair(DirectedPoint(current.point + Point.DOWN, Direction.DOWN), STRAIGHT_SCORE),
                    Pair(DirectedPoint(current.point, Direction.RIGHT), TURN_SCORE),
                    Pair(DirectedPoint(current.point, Direction.LEFT), TURN_SCORE),
                )

                Direction.LEFT -> listOf(
                    Pair(DirectedPoint(current.point + Point.LEFT, Direction.LEFT), STRAIGHT_SCORE),
                    Pair(DirectedPoint(current.point, Direction.UP), TURN_SCORE),
                    Pair(DirectedPoint(current.point, Direction.DOWN), TURN_SCORE),
                )
            }
    }
}
