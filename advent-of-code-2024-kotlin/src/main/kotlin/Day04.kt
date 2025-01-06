package org.fdesande

import org.fdesande.common.FileUtils

class Day04 : AdventProblem {

    companion object {
        private const val INPUT = "/input04.txt"
    }

    override fun firstPart(): String {
        // Valid result: 2468
        return countLinearOccurrences(readFile(INPUT)).toString()
    }

    override fun secondPart(): String {
        // Valid result: 1864
        return countCrossOccurrences(readFile(INPUT)).toString()
    }

    private fun readFile(fileName: String): List<String> {
        return FileUtils.getLines(fileName)
            .filter { line -> line.isNotBlank() }
    }

    private fun countLinearOccurrences(lines: List<String>): Int {
        return lines.indices.map { y ->
            lines[y].indices.map { x -> isLinearXmas(y, x, lines) }
        }.flatten().sum()
    }

    private fun countCrossOccurrences(lines: List<String>): Int {
        var count = 0
        for (y in 1..lines.size - 2) {
            for (x in 1..lines[y].length - 2) {
                if (isCrossXmas(y, x, lines)) {
                    count++
                }
            }
        }

        return count
    }

    private fun isLinearXmas(y: Int, x: Int, lines: List<String>): Int {
        return if (lines[x][y] == 'X') {
            (if (findHorizontal(y, lines[x])) 1 else 0) +
                (if (findHorizontalReverse(y, lines[x])) 1 else 0) +
                (if (findVertical(y, x, lines)) 1 else 0) +
                (if (findVerticalReverse(y, x, lines)) 1 else 0) +
                (if (findCrossToNE(y, x, lines)) 1 else 0) +
                (if (findCrossToNW(y, x, lines)) 1 else 0) +
                (if (findCrossToSE(y, x, lines)) 1 else 0) +
                (if (findCrossToSW(y, x, lines)) 1 else 0)
        } else 0
    }

    private fun findHorizontal(y: Int, line: String): Boolean {
        return try {
            line[y] == 'X' && line[y + 1] == 'M' && line[y + 2] == 'A' && line[y + 3] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun findHorizontalReverse(y: Int, line: String): Boolean {
        return try {
            line[y] == 'X' && line[y - 1] == 'M' && line[y - 2] == 'A' && line[y - 3] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun findVertical(y: Int, x: Int, lines: List<String>): Boolean {
        return try {
            lines[x][y] == 'X' && lines[x + 1][y] == 'M' && lines[x + 2][y] == 'A' && lines[x + 3][y] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun findVerticalReverse(y: Int, x: Int, lines: List<String>): Boolean {
        return try {
            lines[x][y] == 'X' && lines[x - 1][y] == 'M' && lines[x - 2][y] == 'A' && lines[x - 3][y] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun findCrossToNE(y: Int, x: Int, lines: List<String>): Boolean {
        return try {
            lines[x][y] == 'X' && lines[x - 1][y + 1] == 'M' && lines[x - 2][y + 2] == 'A' && lines[x - 3][y + 3] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun findCrossToNW(y: Int, x: Int, lines: List<String>): Boolean {
        return try {
            lines[x][y] == 'X' && lines[x - 1][y - 1] == 'M' && lines[x - 2][y - 2] == 'A' && lines[x - 3][y - 3] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun findCrossToSE(y: Int, x: Int, lines: List<String>): Boolean {
        return try {
            lines[x][y] == 'X' && lines[x + 1][y + 1] == 'M' && lines[x + 2][y + 2] == 'A' && lines[x + 3][y + 3] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun findCrossToSW(y: Int, x: Int, lines: List<String>): Boolean {
        return try {
            lines[x][y] == 'X' && lines[x + 1][y - 1] == 'M' && lines[x + 2][y - 2] == 'A' && lines[x + 3][y - 3] == 'S'
        } catch (ex: IndexOutOfBoundsException) {
            false
        }
    }

    private fun isCrossXmas(y: Int, x: Int, lines: List<String>): Boolean {
        return if (lines[y][x] != 'A') {
            false
        } else {
            val nw = lines[y - 1][x - 1]
            val sw = lines[y + 1][x - 1]
            val ne = lines[y - 1][x + 1]
            val se = lines[y + 1][x + 1]

            listOf(nw, sw, ne, se).all { it == 'M' || it == 'S' } && nw != se && ne != sw
        }
    }
}
