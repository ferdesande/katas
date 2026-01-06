package org.fdesande

import org.fdesande.common.FileUtils

class Day10 : AdventProblem {

    companion object {
        private const val INPUT = "/input10.txt"
    }

    override fun firstPart(): String {
        var value = FileUtils.getLines(INPUT).single() { it.isNotBlank() }

        repeat(40) {value = process(value)}
        return value.length.toString()
    }

    override fun secondPart(): String {
        var value = FileUtils.getLines(INPUT).single() { it.isNotBlank() }

        repeat(50) {value = process(value)}
        return value.length.toString()
    }

    private fun process(value: String): String {
        var i = 0
        val sb = StringBuilder()
        while (i < value.length) {
            val c = value[i]
            var count = 1
            while (i + count < value.length && value[i + count] == c) {
                count++
            }
            sb.append("$count$c")
            i += count
        }
        return sb.toString()
    }
}
