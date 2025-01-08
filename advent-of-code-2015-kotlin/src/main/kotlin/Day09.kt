package org.fdesande

import org.fdesande.common.FileUtils

class Day09 : AdventProblem {

    companion object {
        private const val INPUT = "/input09.txt"
        private val REGEX = Regex("""(\w+) to (\w+) = (\d+)""")
    }

    override fun firstPart(): String {
        // Valid result: 117
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val grid = createGrid(lines)
        return Dijkstra(grid).getShortest().toString()
    }

    override fun secondPart(): String {
        // Valid result: 909
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val grid = createGrid(lines)
        return Dijkstra(grid).getLongest().toString()
    }

    private fun createGrid(lines: List<String>): Map<String, Map<String, Int>> {
        val distances = mutableMapOf<String, MutableMap<String, Int>>()
        lines.mapNotNull { line -> REGEX.find(line) }
            .forEach { match ->
                val origin = match.groupValues[1]
                val destination = match.groupValues[2]
                val distance = match.groupValues[3].toInt()

                if (origin in distances.keys) {
                    distances[origin]!![destination] = distance
                } else {
                    distances[origin] = mutableMapOf(destination to distance)
                }

                if (destination in distances.keys) {
                    distances[destination]!![origin] = distance
                } else {
                    distances[destination] = mutableMapOf(origin to distance)
                }
            }
        return distances
    }

    private class Dijkstra(private val grid: Map<String, Map<String, Int>>) {
        private var distance = Int.MAX_VALUE
        private var path: Set<String> = setOf()

        fun getShortest(): Int {
            // As the weight of the transition does not depend on the direction, We decide the first element.
            distance = Int.MAX_VALUE
            visitNextForShortest(setOf())
            return distance
        }

        fun getLongest(): Int {
            // As the weight of the transition does not depend on the direction, We decide the first element.
            distance = 0
            visitNextForLongest(setOf())
            return distance
        }

        private fun visitNextForShortest(visited: Set<String>) {
            if (visited.size == grid.keys.size) {
                val distance = visited.zipWithNext { a, b -> grid[a]!![b]!! }.sum()
                if (distance < this.distance) {
                    this.distance = distance
                    path = visited
                }
            }

            val pending = grid.keys - visited
            for (next in pending) {
                visitNextForShortest(visited + next)
            }
        }

        private fun visitNextForLongest(visited: Set<String>) {
            if (visited.size == grid.keys.size) {
                val distance = visited.zipWithNext { a, b -> grid[a]!![b]!! }.sum()
                if (distance > this.distance) {
                    this.distance = distance
                    path = visited
                }
            }

            val pending = grid.keys - visited
            for (next in pending) {
                visitNextForLongest(visited + next)
            }
        }

    }
}
