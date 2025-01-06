package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Point

class Day03 : AdventProblem {

    companion object {
        private const val INPUT = "/input03.txt"
    }

    override fun firstPart(): String {
        // Valid result: 2565
        val line = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        var currenPoint = Point(0, 0)
        val visited = mutableSetOf(currenPoint)
        line.forEach { dir ->
            currenPoint += getMove(dir)
            visited += currenPoint
        }

        return visited.size.toString()
    }

    override fun secondPart(): String {
        // Valid result: 2639
        val line = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        var santa = Point(0, 0)
        var robot = Point(0, 0)
        val visited = mutableSetOf(santa)
        line.forEachIndexed { index, dir ->
            if (index % 2 == 0) {
                santa += getMove(dir)
                visited += santa
            } else {
                robot += getMove(dir)
                visited += robot
            }
        }
        return visited.size.toString()
    }

    private fun getMove(c: Char): Point =
        when (c) {
            '<' -> Point.LEFT
            '>' -> Point.RIGHT
            '^' -> Point.UP
            'v' -> Point.DOWN
            else -> Point(0, 0)
        }
}
