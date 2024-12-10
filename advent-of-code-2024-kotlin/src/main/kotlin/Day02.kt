package org.fdesande

class Day02 : AdventProblem {

    companion object {
        private const val FIRST_INPUT = "/input02-1.txt"
        private const val SECOND_INPUT = "/input02-2.txt"
    }

    override fun firstPart(): String {
        // Valid result: 510
        return readFile(FIRST_INPUT).count { report -> isValidReport(report) }.toString()
    }

    override fun secondPart(): String {
        // Valid result: 553
        return readFile(SECOND_INPUT)
            .count { report -> isValidReportAdvanced(report) }.toString()
    }

    private fun isValidReport(report: List<Int>): Boolean {
        val distances = report.zipWithNext { a, b -> b - a }
        return distances.all { it in 1..3 } || distances.all { it in -3..-1 }
    }

    private fun isValidReportAdvanced(report: List<Int>): Boolean {
        if (isValidReport(report)) return true

        report.indices.forEach { i ->
            val list = report.subList(0, i) + report.subList(i + 1, report.size)
            if (isValidReport(list)) return true
        }

        return false
    }

    private fun readFile(fileName: String): List<List<Int>> {
        val fileText = this.javaClass.getResource(fileName)?.readText()
            ?: throw IllegalStateException("File not found: $fileName")
        return fileText.split("\n")
            .filter { line -> line.isNotBlank() }
            .map { line -> line.split(Regex("\\s+")) }
            .map { items -> items.map { item -> item.toInt() } }
    }
}
