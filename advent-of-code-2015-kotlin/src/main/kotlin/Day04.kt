package org.fdesande

import org.fdesande.common.FileUtils
import java.security.MessageDigest

class Day04 : AdventProblem {

    companion object {
        private const val INPUT = "/input04.txt"
        private const val LIMIT = 100000000
    }

    override fun firstPart(): String {
        // Valid result: 254575
        return findNonce("00000")
    }

    override fun secondPart(): String {
        // Valid result: 1038736
        return findNonce("000000")
    }

    private fun findNonce(mask: String): String {
        val line = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        var nonce = 0
        while (nonce < LIMIT) {
            val hash = calculateMD5("$line$nonce")
            if (hash.startsWith(mask))
                return nonce.toString()
            nonce++
        }

        return "-1"
    }

    private fun calculateMD5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
