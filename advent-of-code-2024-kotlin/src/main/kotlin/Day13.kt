package org.fdesande

import org.fdesande.common.FileUtils

class Day13 : AdventProblem {

    companion object {
        private const val INPUT = "/input13.txt"
        private const val SHIFT = 10000000000000L

        private val SHIFT_POINT = LongPoint(SHIFT, SHIFT)
        private val RANGE = 0..100
    }

    override fun firstPart(): String {
        // Valid result: 29436
        val lines = FileUtils.getLines(INPUT)
        return extractMachines(lines)
            .mapNotNull { solveMachine(it) }
            .filter { solution -> solution.aCount in RANGE && solution.bCount in RANGE }
            .sumOf { 3 * it.aCount + it.bCount }.toString()
    }

    // Valid result:
    override fun secondPart(): String {
        // Valid result: 103729094227877
        val lines = FileUtils.getLines(INPUT)
        val a = extractMachines(lines)
            .map { machine -> Machine(machine.prize + SHIFT_POINT, machine.buttonA, machine.buttonB) }
            .mapNotNull { solveMachine(it) }
        return a.sumOf { 3 * it.aCount + it.bCount }.toString()
    }

    private fun extractMachines(lines: List<String>): List<Machine> {
        val result = mutableListOf<Machine>()

        val regex = Regex("X.(\\d+), Y.(\\d+)")
        var a = ButtonShift()
        var b = ButtonShift()
        for (line in lines) {
            val match = regex.find(line)
            if (match != null) {
                val x = match.groupValues[1].toLong()
                val y = match.groupValues[2].toLong()
                when {
                    line.startsWith("Button A:") -> a = ButtonShift(x, y)
                    line.startsWith("Button B:") -> b = ButtonShift(x, y)
                    line.startsWith("Prize:") -> result.add(Machine(LongPoint(x, y), a, b))
                }
            }
        }
        return result
    }

    private fun solveMachine(machine: Machine): MachineSolution? {
        val matrix = arrayOf(
            arrayOf(machine.buttonA.shiftX, machine.buttonB.shiftX, machine.prize.x),
            arrayOf(machine.buttonA.shiftY, machine.buttonB.shiftY, machine.prize.y)
        )

        return Equation(matrix).solve()
            ?.let { MachineSolution(aCount = it.first, bCount = it.second) }
            ?.let { solution -> if (machine.isValidSolution(solution)) solution else null }
    }

    private data class LongPoint(val x: Long = 0, val y: Long = 0) {
        operator fun plus(other: LongPoint) = LongPoint(x + other.x, y + other.y)
    }

    private data class ButtonShift(val shiftX: Long = 0, val shiftY: Long = 0)
    private data class MachineSolution(val aCount: Long, val bCount: Long)
    private data class Machine(val prize: LongPoint, val buttonA: ButtonShift, val buttonB: ButtonShift) {
        fun isValidSolution(solution: MachineSolution): Boolean =
            buttonA.shiftX * solution.aCount + buttonB.shiftX * solution.bCount == prize.x
                && buttonA.shiftY * solution.aCount + buttonB.shiftY * solution.bCount == prize.y
    }

    private class Equation(private val matrix: Array<Array<Long>>) {
        fun solve(): Pair<Long, Long>? {
            val det = getDeterminant(matrix)
            if (det == 0L) return null

            val solution = matrix.indices
                .map { column -> getKramerMatrix(matrix, column) }
                .map { kramerMatrix -> getDeterminant(kramerMatrix) / det }
            return Pair(solution[0], solution[1])
        }

        private fun getKramerMatrix(matrix: Array<Array<Long>>, column: Int): Array<Array<Long>> =
            matrix.indices.map { y ->
                matrix.indices.map { x -> if (x == column) matrix[y][2] else matrix[y][x] }.toTypedArray()
            }.toTypedArray()

        private fun getDeterminant(matrix: Array<Array<Long>>): Long =
            matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1]
    }
}
