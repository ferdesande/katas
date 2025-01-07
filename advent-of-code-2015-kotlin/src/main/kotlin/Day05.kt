package org.fdesande

import org.fdesande.common.FileUtils

class Day05 : AdventProblem {

    companion object {
        private const val INPUT = "/input05.txt"
        private val FORBIDDEN_WORDS = setOf("ab", "cd", "pq", "xy")
        private val VOWELS = setOf('a', 'e', 'i', 'o', 'u')
    }

    override fun firstPart(): String {
        // Valid result: 238
        return FileUtils.getLines(INPUT).filter { it.isNotBlank() }
            .count { word ->
                val vowelCount = word.count { it in VOWELS }
                val repeatedInARow = word.zipWithNext { a, b -> a == b }.any { it }
                val containForbidden = FORBIDDEN_WORDS.any { word.contains(it) }
                vowelCount >= 3 && repeatedInARow && !containForbidden
            }.toString()
    }

    override fun secondPart(): String {
        // Valid result: 69
        return FileUtils.getLines(INPUT).filter { it.isNotBlank() }
            .count { word ->
                val pairs = (0 until word.lastIndex).any { i ->
                    val pair = "${word[i]}${word[i + 1]}"
                    word.indexOf(pair, i + 2) > -1
                }
                val repeatedInterleaved = (2..word.lastIndex).any { i -> word[i] == word[i - 2] }
                repeatedInterleaved && pairs
            }.toString()
    }
}
