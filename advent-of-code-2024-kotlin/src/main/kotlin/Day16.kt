package org.fdesande

import org.fdesande.common.Direction
import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.Point

class Day16 : AdventProblem {

    companion object {
        private const val INPUT = "/input16.txt"
        private const val SAMPLE = "/input16-sample.txt"

        private const val START = 'S'
        private const val END = 'E'
        private const val WALL = '#'

        private const val STRAIGHT_SCORE = 1
        private const val TURN_SCORE = 1001
    }

    override fun firstPart(): String {
        // Valid result: 106512
        val grid = FileUtils.getGrid(INPUT)
        return getBestPaths(grid).minOfOrNull { it.last().score }.toString()
    }

    override fun secondPart(): String {
        // Valid result:
        val grid = FileUtils.getGrid(SAMPLE)
        TODO()
    }

    private fun getBestPaths(grid: Grid): List<Set<MazePoint>> {
        val visited = mutableMapOf<Point, Int>()
        val start = grid.getSingleCharacterPoint(START)
        var paths = setOf(setOf(MazePoint(start, Direction.RIGHT, 0)))
        var bestScore = Int.MAX_VALUE
        var bestPaths: MutableList<Set<MazePoint>> = mutableListOf()

        while (paths.isNotEmpty()) {
            val pathsWithScoreByPoint = paths
                .filterNot { path -> grid.getValue(path.last().point) == END }
                .flatMap { path ->
                    path.last().getNeighbors()
                        .asSequence()
                        .filter { grid.isInGrid(it.point) }
                        .filterNot { grid.getValue(it.point) == WALL }
                        .filter { (visited[it.point] ?: Int.MAX_VALUE) > it.score }
                        .filterNot { bestScore <= it.score }
                        .map { path + it }
                        .toList()
                }
                .groupBy({ it.last().point }, { Pair(it, it.last().score) })
            val minScoreByPoint = pathsWithScoreByPoint.mapValues { pairs -> pairs.value.minOf { it.second } }
            paths = pathsWithScoreByPoint.entries
                .flatMap { entry ->
                    entry.value
                        .filter { it.second == minScoreByPoint[entry.key] }
                        .map { pair -> pair.first }
                }
                .toSet()

            val justVisited = paths.map { it.last() }
            visited += justVisited.map { it.point to it.score }
            paths
                .filter { grid.getValue(it.last().point) == END }
                .forEach { path ->
                    val score = path.last().score
                    if (score == bestScore) {
                        bestPaths += path
                    } else if (score < bestScore) {
                        bestPaths = mutableListOf(path)
                        bestScore = score
                    }
                }
        }
        return bestPaths
    }

    private data class MazePoint(val point: Point, val direction: Direction, val score: Int) {
        fun getNeighbors(): List<MazePoint> {
            return when (direction) {
                Direction.UP -> listOf(
                    MazePoint(point + Point.UP, Direction.UP, score + STRAIGHT_SCORE),
                    MazePoint(point + Point.RIGHT, Direction.RIGHT, score + TURN_SCORE),
                    MazePoint(point + Point.LEFT, Direction.LEFT, score + TURN_SCORE),
                )

                Direction.RIGHT -> listOf(
                    MazePoint(point + Point.RIGHT, Direction.RIGHT, score + STRAIGHT_SCORE),
                    MazePoint(point + Point.DOWN, Direction.DOWN, score + TURN_SCORE),
                    MazePoint(point + Point.UP, Direction.UP, score + TURN_SCORE),
                )

                Direction.DOWN -> listOf(
                    MazePoint(point + Point.DOWN, Direction.DOWN, score + STRAIGHT_SCORE),
                    MazePoint(point + Point.RIGHT, Direction.RIGHT, score + TURN_SCORE),
                    MazePoint(point + Point.LEFT, Direction.LEFT, score + TURN_SCORE),
                )

                Direction.LEFT -> listOf(
                    MazePoint(point + Point.LEFT, Direction.LEFT, score + STRAIGHT_SCORE),
                    MazePoint(point + Point.UP, Direction.UP, score + TURN_SCORE),
                    MazePoint(point + Point.DOWN, Direction.DOWN, score + TURN_SCORE),
                )
            }
        }
    }
}
