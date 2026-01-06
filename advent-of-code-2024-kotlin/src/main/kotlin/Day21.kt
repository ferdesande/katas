package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Point

class Day21 : AdventProblem {

    companion object {
        private const val INPUT = "/input21.txt"
    }

    private val pathFinder = BestPathFinder()

    override fun firstPart(): String {
        val values = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val numericValue = values.map { it.dropLast(1) }.map { it.toInt() }

        return values
            .map { numPadCode -> pathFinder.getDirectionPadCode("A$numPadCode") }
            .map { directionalPadCode -> pathFinder.getBestPath(directionalPadCode, 2) }
            .zip(numericValue)
            .sumOf { it.first * it.second }.toString()
    }

    override fun secondPart(): String {
        val values = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val numericValue = values.map { it.dropLast(1) }.map { it.toInt() }

        return values
            .map { numPadCode -> pathFinder.getDirectionPadCode("A$numPadCode") }
            .map { directionalPadCode -> pathFinder.expandDirectionalPadCode(directionalPadCode, 1) }
            .map { directionalPadCode -> pathFinder.expandDirectionalPadCode("A$directionalPadCode", 12) }
            .map { directionalPadCode -> pathFinder.getBestPath("A$directionalPadCode", 12) }
            .zip(numericValue)
            .sumOf { it.first * it.second }.toString()
    }

    private enum class Direction { UP, DOWN, LEFT, RIGHT }

    private class BestPathFinder {
        companion object {
            private val numericKeyboard = mapOf(
                '0' to Point(1, 3),
                'A' to Point(2, 3),
                '1' to Point(0, 2),
                '2' to Point(1, 2),
                '3' to Point(2, 2),
                '4' to Point(0, 1),
                '5' to Point(1, 1),
                '6' to Point(2, 1),
                '7' to Point(0, 0),
                '8' to Point(1, 0),
                '9' to Point(2, 0),
            )
            private val directionalKeyboard = mapOf(
                '^' to Point(1, 0),
                'A' to Point(2, 0),
                '<' to Point(0, 1),
                'v' to Point(1, 1),
                '>' to Point(2, 1),
            )
        }

        private val numericKeyboardPathStrings: Map<Pair<Point, Point>, String>
        private val bestPathsByDepth: MutableMap<Int, Map<Pair<Point, Point>, String>> = mutableMapOf()

        init {
            val numericKeyboardPaths = mapPath(numericKeyboard.values.toSet())
            val directionalKeyboardPaths = mapPath(directionalKeyboard.values.toSet())

            numericKeyboardPathStrings = numericKeyboardPaths.mapValues { entry ->
                entry.value
                    .map { path ->
                        path.zipWithNext { a, b -> goTo(a, b) }
                            .mapNotNull { it }
                            .joinToString("")
                    }.map { "${it}A" }
            }.mapValues { entry -> calculateBestPath(entry.value) }

            val directionalKeyboardPathStrings = directionalKeyboardPaths.mapValues { entry ->
                entry.value
                    .map { path ->
                        path.zipWithNext { a, b -> goTo(a, b) }
                            .mapNotNull { it }
                            .joinToString("")
                    }
                    .map { "${it}A" }
            }.mapValues { entry -> calculateBestPath(entry.value) }

            bestPathsByDepth[1] = directionalKeyboardPathStrings
        }

        fun getDirectionPadCode(numPadCode: String): String =
            "A$numPadCode"
                .zipWithNext { a, b -> Pair(numericKeyboard[a], numericKeyboard[b]) }
                .mapNotNull { numericKeyboardPathStrings[it] }
                .joinToString("")

        fun getBestPath(directionalPadCode: String, depth: Int): Long {
            expand(depth)

            return directionalPadCode
                .zipWithNext { a, b -> Pair(directionalKeyboard[a], directionalKeyboard[b]) }
                .sumOf { (bestPathsByDepth[depth]?.get(it)?.length ?: 0).toLong() }
        }

        fun expandDirectionalPadCode(directionalPadCode: String, depth: Int): String {
            expand(depth)

            return directionalPadCode
                .zipWithNext { a, b -> Pair(directionalKeyboard[a], directionalKeyboard[b]) }
                .mapNotNull { bestPathsByDepth[depth]?.get(it) }
                .joinToString("")
        }

        private fun expand(depth: Int) {
            for (i in 1..depth) {
                if (!bestPathsByDepth.contains(i)) {
                    val path = bestPathsByDepth[i - 1]!!
                    bestPathsByDepth[i] = path.mapValues { entry ->
                        "A${entry.value}"
                            .zipWithNext { a, b -> Pair(directionalKeyboard[a], directionalKeyboard[b]) }
                            .mapNotNull { bestPathsByDepth[1]?.get(it) }
                            .joinToString("")
                    }
                }
            }
        }

        private fun goTo(origin: Point, destination: Point): String? {
            return when (destination - origin) {
                Point(0, 1) -> "v"
                Point(0, -1) -> "^"
                Point(1, 0) -> ">"
                Point(-1, 0) -> "<"
                Point(0, 0) -> "A"
                else -> null
            }
        }

        private fun mapPath(validPoints: Set<Point>): Map<Pair<Point, Point>, Set<Set<Point>>> {
            val result = mutableMapOf<Pair<Point, Point>, Set<Set<Point>>>()
            for (origin in validPoints) {
                for (destination in validPoints) {
                    if (origin != destination) {
                        result[Pair(origin, destination)] = getPaths(origin, destination, validPoints)
                    } else {
                        result[Pair(origin, destination)] = setOf(setOf(origin))
                    }
                }
            }
            return result
        }

        private fun getPaths(origin: Point, destination: Point, validPoints: Set<Point>): Set<Set<Point>> {
            var current = setOf(setOf(origin))
            while (current.isNotEmpty()) {
                current = current.flatMap { path ->
                    path.last().getNeighbors()
                        .filter { validPoints.contains(it) }
                        .map { path + it }
                        .toSet()
                }.toSet()

                val next = current.filter { it.last() == destination }
                if (next.isNotEmpty()) {
                    return next.toSet()
                }
            }
            return emptySet()
        }

        private fun calculateBestPath(paths: List<String>): String {
            if (paths.size == 1)
                return paths.first()

            val options = paths.map { Pair(it, getDirections(it)) }
            val minDirectionChanges = options.minOf { it.second.size }
            val bestOptions = options
                .filter { it.second.size == minDirectionChanges }

            val upLeft = bestOptions.firstOrNull { it.second == listOf(Direction.LEFT, Direction.UP) }?.first
            val downLeft = bestOptions.firstOrNull { it.second == listOf(Direction.LEFT, Direction.DOWN) }?.first
            val upRight = bestOptions.firstOrNull { it.second == listOf(Direction.UP, Direction.RIGHT) }?.first
            val downRight = bestOptions.firstOrNull { it.second == listOf(Direction.DOWN, Direction.RIGHT) }?.first

            return if (bestOptions.size == 1) {
                bestOptions.first().first
            } else {
                upLeft ?: downLeft ?: upRight ?: downRight ?: throw IllegalStateException("Algorithm failed")
            }
        }

        private fun getDirections(path: String): List<Direction> =
            path.mapNotNull { c ->
                when (c) {
                    '<' -> Direction.LEFT
                    '^' -> Direction.UP
                    '>' -> Direction.RIGHT
                    'v' -> Direction.DOWN
                    else -> null
                }
            }.zipWithNext { a, b -> if (a == b) null else a }
                .filterNotNull() + path.mapNotNull { c ->
                when (c) {
                    '<' -> Direction.LEFT
                    '^' -> Direction.UP
                    '>' -> Direction.RIGHT
                    'v' -> Direction.DOWN
                    else -> null
                }
            }.last()
    }
}
