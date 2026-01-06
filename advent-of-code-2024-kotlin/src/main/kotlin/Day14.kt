package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Point

class Day14 : AdventProblem {

    companion object {
        private const val INPUT = "/input14.txt"

        private const val MAX_X = 100
        private const val MAX_Y = 102
    }

    override fun firstPart(): String {
        val lines = FileUtils.getLines(INPUT)
        var robots = extractRobots(lines)
        repeat(100) { robots = robots.map { robot -> move(robot) } }
        val a = robotsInQuadrant(robots)
        return a
            .filter { it.isNotEmpty() }
            .map { robot -> robot.size }
            .reduce { acc, size -> acc * size }
            .toString()
    }

    override fun secondPart(): String {
        val lines = FileUtils.getLines(INPUT)
        var robots = extractRobots(lines).toSet()
        var count = 0
        while (!areAllPointContiguous(robots) && count < 100000) {
            robots = robots.map { robot -> move(robot) }.toSet()
            count++
        }

        println(printRobots(robots))
        return count.toString()
    }

    private fun areAllPointContiguous(robots: Set<Robot>): Boolean {
        val positions = robots.map { it.position }.sortedBy { it.x }.sortedBy { it.y }.toSet()
        return robots.any { robot -> (1..10).all { x -> (robot.position + Point(x, 0)) in positions } }
    }

    private fun extractRobots(lines: List<String>): List<Robot> =
        lines
            .mapNotNull { line -> Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)").find(line) }
            .map { match ->
                Robot(
                    Point(match.groupValues[1].toInt(), match.groupValues[2].toInt()),
                    Point(match.groupValues[3].toInt(), match.groupValues[4].toInt()),
                )
            }

    private fun move(robot: Robot): Robot {
        val newPosition = robot.position + robot.speed
        return Robot(Point(adjustPosition(newPosition.x, MAX_X), adjustPosition(newPosition.y, MAX_Y)), robot.speed)
    }

    private fun adjustPosition(position: Int, max: Int): Int {
        return if (position < 0) max + position + 1
        else if (position > max) position - max - 1
        else position
    }

    private fun robotsInQuadrant(robots: List<Robot>): List<List<Robot>> {
        val midX = MAX_X / 2
        val midY = MAX_Y / 2

        val west = 0 until midX
        val east = midX + 1..MAX_X
        val north = 0 until midY
        val south = midY + 1..MAX_Y

        return listOf(
            robots.filter { it.position.y in north && it.position.x in west },
            robots.filter { it.position.y in north && it.position.x in east },
            robots.filter { it.position.y in south && it.position.x in west },
            robots.filter { it.position.y in south && it.position.x in east },
        )
    }

    private fun printRobots(robots: Set<Robot>): String {
        val positions = robots.map { it.position }.toSet()
        return (0..MAX_Y).joinToString("\n") { y ->
            (0..MAX_X).map { x -> if (Point(x, y) in positions) 'X' else '.' }.joinToString("")
        }
    }

    private data class Robot(val position: Point, val speed: Point)
}
