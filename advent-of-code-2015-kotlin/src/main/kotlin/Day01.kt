package org.fdesande

import org.fdesande.common.FileUtils

class Day01 : AdventProblem {

    companion object {
        private const val INPUT = "/input01.txt"
    }

    override fun firstPart(): String {
        val line = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        return line.map {
            when (it) {
                '(' -> 1
                ')' -> -1
                else -> 0
            }
        }.sum().toString()
    }

    override fun secondPart(): String {
        val line = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        var floor = 0

        line.forEachIndexed { index, c ->
            floor += when (c) {
                '(' -> 1
                ')' -> -1
                else -> 0
            }
            if (floor == -1) {
                return (index + 1).toString()
            }
        }

        return "-1"
    }
}
