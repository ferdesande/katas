package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Point

class Day21 : AdventProblem {

    companion object {
        private const val INPUT = "/input21.txt"
        private const val SAMPLE = "/input21-sample.txt"

    }

    private val pathFinder = BestPathFinder()

    override fun firstPart(): String {
        // Valid result: 176650
        val values = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val numericValue = values.map { it.dropLast(1) }.map { it.toInt() }
        return values
            .map { "A$it" }
            .map { path ->
                path.zipWithNext { origin, destination -> pathFinder.getBestPath(origin, destination) }
                    .joinToString("")
            }
            .zip(numericValue).map { (a, b) -> Pair(a.length, b) }.sumOf { it.first * it.second }.toString()
    }

    override fun secondPart(): String {
        // Valid result:
        val lines = FileUtils.getGrid(SAMPLE)
        TODO()
    }

    private class BestPathFinder() {
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

            private val numericKeyboardPaths = mapPath(numericKeyboard.values.toSet())
            private val directionalKeyboardPaths = mapPath(directionalKeyboard.values.toSet())

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
        }

        private val bestPath: Map<Pair<Point, Point>, String>

        private fun getNextRobotPaths(
            paths: List<String>,
            pathByRoute: Map<Pair<Point, Point>, List<String>>
        ): List<List<List<String>>> = paths.map { "A$it" }
            .map { path ->
                path.zipWithNext { a, b -> Pair(directionalKeyboard[a]!!, directionalKeyboard[b]!!) }
                    .mapNotNull { pair -> pathByRoute[pair] }
            }

        init {

            val numericKeyboardPathStrings = numericKeyboardPaths.mapValues { entry ->
                entry.value
                    .map { path ->
                        path.zipWithNext { a, b -> goTo(a, b) }
                            .mapNotNull { it }
                            .joinToString("")
                    }.map { "${it}A" }
            }

            val directionalKeyboardPathStrings = directionalKeyboardPaths.mapValues { entry ->
                entry.value
                    .map { path ->
                        path.zipWithNext { a, b -> goTo(a, b) }
                            .mapNotNull { it }
                            .joinToString("")
                    }
                    .map { "${it}A" }
            }

            val firstRobot = numericKeyboardPathStrings
                .mapValues { entry -> getNextRobotPaths(entry.value, directionalKeyboardPathStrings) }

            val secondRobot = firstRobot
                .mapValues { entry ->
                    entry.value.map { paths ->
                        paths.map { path -> getNextRobotPaths(path, directionalKeyboardPathStrings) }
                    }
                }

            bestPath = secondRobot.mapValues { entry ->
                entry.value.map { numeric ->
                    numeric.map { robot1 ->
                        robot1.map { robot2 ->
                            robot2.map { paths -> paths.minBy { path -> path.length } }.joinToString("")
                        }
                    }.map { robot1 -> robot1.minBy { path -> path.length } }.joinToString("")
                }.minBy { path -> path.length }

            }
        }

        fun getBestPath(origin: Char, destination: Char): String =
            bestPath[Pair(numericKeyboard[origin], numericKeyboard[destination])]!!

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
    }
}
