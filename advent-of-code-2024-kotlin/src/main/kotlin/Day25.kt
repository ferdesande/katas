package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Grid

class Day25 : AdventProblem {

    companion object {
        private const val INPUT = "/input25.txt"
    }

    override fun firstPart(): String {
        val lines = FileUtils.getLines(INPUT)
        val emptyLines = lines
            .mapIndexed { index, s -> if (s.isBlank()) index else null }
            .filterNotNull()
            .toMutableList()

        emptyLines.add(0, -1)
        val blocks = emptyLines.zipWithNext { a, b -> Pair(a + 1, b) }
        val grids = blocks.map { lines.subList(it.first, it.second) }.map { Grid(it) }
        val locks = grids.filter { it.isLock }.map { getLockHeights(it) }
        val keys = grids.filter { it.isKey }.map { getKeyHeights(it) }
        return locks.flatMap { lock ->
            keys.map { key -> key.zip(lock).all { it.first + it.second <= 5 } }
        }.count { it }.toString()
    }

    override fun secondPart(): String {
        return "No second part for this puzzle"
    }

    private val Grid.isKey: Boolean
        get() {
            return this.getContent().filter { it.first.y == 0 }.all { it.second == '.' }
        }

    private val Grid.isLock: Boolean
        get() {
            return this.getContent().filter { it.first.y == 0 }.all { it.second == '#' }
        }

    private fun getKeyHeights(grid: Grid): List<Int> = countPoints(grid)
    private fun getLockHeights(grid: Grid): List<Int> = countPoints(grid)

    private fun countPoints(grid: Grid): List<Int> {
        val bounds = grid.getBounds()
        val validCells = grid.getContent()
            .filter { it.first.y in (bounds.minY + 1)..<bounds.maxY }

        return (bounds.minX..bounds.maxX)
            .map { x -> validCells.filter { it.first.x == x }.count { it.second == '#' } }
    }
}
