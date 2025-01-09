package org.fdesande

import org.fdesande.common.FileUtils

class Day11 : AdventProblem {

    companion object {
        private const val INPUT = "/input11.txt"
        private const val VALID_CHARS = "abcdefghijklmnopqrstuvwxyz"
        private val POS_BY_CHAR = VALID_CHARS.mapIndexed { index, c -> c to index }.toMap()
    }

    private var firstPassword = ""

    override fun firstPart(): String {
        // Valid result: (original) hxbxxyzz
        val password = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        firstPassword = getNextValidPassword(password)
        return firstPassword
    }

    override fun secondPart(): String {
        // Valid result: hxcaabcc
        return getNextValidPassword(firstPassword)
    }

    private fun getNextValidPassword(password: String): String {
        var next = increasePassword(password)
        while (!isValidPassword(next)) {
            next = increasePassword(next)
        }
        return next
    }

    private fun isValidPassword(password: String): Boolean {
        val first = grantsThreeInARow(password)
        val second = grantsSecondCondition(password)
        val third = grantsThirdCondition(password)

        return first && second && third
    }

    private fun grantsThreeInARow(password: String): Boolean {
        var firstMatch = false
        for (i in 0..<password.lastIndex) {
            firstMatch = if (POS_BY_CHAR[password[i + 1]]!! - POS_BY_CHAR[password[i]]!! == 1) {
                if (firstMatch)
                    return true
                else
                    true
            } else
                false
        }

        return false
    }

    private fun grantsSecondCondition(password: String): Boolean = !password.contains(Regex("[il]"))
    private fun grantsThirdCondition(password: String): Boolean {
        val found = mutableSetOf<Char>()
        var i = 0
        while (i < password.lastIndex) {
            if (password[i] == password[i + 1]) {
                var j = i + 2
                while (j < password.lastIndex && password[i] == password[j]) {
                    j++
                }
                if (j == i + 2)
                    found.add(password[i])
                i = j
            } else
                i++
        }

        return found.size >= 2
    }

    private fun increasePassword(password: String): String {
        var carry = 1
        val reversed = password.reversed()
        val increased = reversed.map { c ->
            val pos = (POS_BY_CHAR[c]!! + carry) % VALID_CHARS.length
            carry = if (pos == 0 && carry == 1) 1 else 0
            when (val next = VALID_CHARS[pos]) {
                'i' -> 'j'
                'l' -> 'm'
                else -> next
            }
        }.joinToString("")
        val result = increased.reversed()
        return result
    }
}
