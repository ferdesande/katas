package org.fdesande.common

import java.io.FileNotFoundException

class FileUtils {
    companion object {
        fun getLines(filePath: String): List<String> =
            FileUtils::class.java.getResource(filePath)?.readText()?.split("\n")
                ?: throw FileNotFoundException("File not found: $filePath")

        fun getGrid(filePath: String): Grid = Grid(getLines(filePath).filter { it.isNotBlank() })
    }
}
