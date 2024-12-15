package org.fdesande.common

import java.io.FileNotFoundException

class FileUtils {
    companion object {
        fun getLines(filePath: String): List<String> {
            return FileUtils::class.java.getResource(filePath)?.readText()?.split("\n")
                ?: throw FileNotFoundException("File not found: $filePath")
        }
    }
}
