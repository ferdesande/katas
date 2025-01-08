package org.fdesande

import org.fdesande.common.FileUtils

class Day08 : AdventProblem {

    companion object {
        private const val INPUT = "/input08.txt"
    }

    override fun firstPart(): String {
        // Valid result: 1333
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val contents = lines.map { line -> getLineContents(line) }

        return (lines.sumOf { it.length } - contents.sumOf { it.length }).toString()
    }

    override fun secondPart(): String {
        // Valid result: 2046
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val contents = lines.map { line -> encode(line) }

        return (contents.sumOf { it.length } - lines.sumOf { it.length }).toString()
    }

    private fun getLineContents(line: String): String {
        var i = 1
        val sb = StringBuilder()
        while (i < line.lastIndex) {
            if (line[i] == '\\') {
                if (line[i + 1] == 'x') {
                    val code = line.substring(i + 2, i + 3)
                    sb.append(code.toInt(16).toChar())
                    i += 4
                } else {
                    sb.append(line[i + 1])
                    i += 2
                }
            } else
                sb.append(line[i++])
        }
        return sb.toString()
    }

    private fun encode(line: String): String {
        val sb = StringBuilder()
        for (i in line.indices) {
            if (line[i] == '\\')
                sb.append("\\\\")
            else if (line[i] == '\"')
                sb.append("\\\"")
            else
                sb.append(line[i])
        }
        return '"' + sb.toString() + '"'
    }

}
