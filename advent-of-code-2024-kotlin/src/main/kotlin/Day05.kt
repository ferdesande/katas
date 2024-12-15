package org.fdesande

import org.fdesande.common.FileUtils

class Day05 : AdventProblem {

    companion object {
        private const val FIRST_INPUT = "/input05-1.txt"
        private const val SECOND_INPUT = "/input05-2.txt"
    }

    override fun firstPart(): String {
        // Valid result: 7307
        val fileBlocks = readFile(FIRST_INPUT)

        val nextPagesByPage = getNextPagesByPage(fileBlocks)
        return getUpdates(fileBlocks)
            .filter { update -> isValidUpdate(update, nextPagesByPage) }
            .sumOf { update -> update[(update.size) / 2] }
            .toString()
    }

    override fun secondPart(): String {
        // Valid result: 4713
        val fileBlocks = readFile(SECOND_INPUT)

        val nextPagesByPage = getNextPagesByPage(fileBlocks)
        return getUpdates(fileBlocks)
            .filter { update -> !isValidUpdate(update, nextPagesByPage) }
            .map { update -> fixIndices(update, nextPagesByPage) }
            .sumOf { update -> update[(update.size) / 2] }
            .toString()
    }

    private fun getUpdates(fileBlocks: FileBlocks) = fileBlocks.secondBlock
        .map { line -> line.split(',').map(String::toInt) }

    private fun getNextPagesByPage(fileBlocks: FileBlocks) = fileBlocks.firstBlock
        .map { line -> line.split('|').map(String::toInt) }
        .groupBy({ it[0] }, { it[1] })

    private fun isValidUpdate(update: List<Int>, nextPagesByPage: Map<Int, List<Int>>): Boolean {
        for (i in 1..update.lastIndex) {
            val nextPages = nextPagesByPage[update[i]]?.toSet() ?: setOf()
            if (update.subList(0, i).intersect(nextPages).isNotEmpty()) {
                return false
            }
        }

        return true
    }

    private fun isFirstElementInvalid(update: List<Int>, nextPagesByPage: Map<Int, List<Int>>): Boolean {
        val firstElement = update.firstOrNull() ?: return true
        for (i in 1..update.lastIndex) {
            val nextPages = nextPagesByPage[update[i]]?.toSet() ?: setOf()
            if (nextPages.contains(firstElement)) {
                return false
            }
        }

        return true
    }

    private fun fixIndices(update: List<Int>, nextPagesByPage: Map<Int, List<Int>>): List<Int> {
        val fixedUpdates = mutableListOf<Int>()
        for (i in update.indices) {
            val remainingPages = update.filter { !fixedUpdates.contains(it) }
            val tmpPages = remainingPages.toMutableList()
            for (j in tmpPages.indices) {
                if (isFirstElementInvalid(tmpPages, nextPagesByPage)) {
                    fixedUpdates.add(tmpPages[0])
                    break
                } else {
                    tmpPages.removeFirst()
                }
            }
        }

        return fixedUpdates
    }

    private fun readFile(fileName: String): FileBlocks {
        val lines = FileUtils.getLines(fileName)

        val firstBlock = mutableListOf<String>()
        val secondBlock = mutableListOf<String>()
        var separatorFound = false
        lines.forEach { line ->
            if (line.isNotBlank()) {
                if (separatorFound) secondBlock.add(line) else firstBlock.add(line)
            } else {
                if (separatorFound) {
                    return@forEach
                } else {
                    separatorFound = true
                }
            }
        }
        return FileBlocks(firstBlock, secondBlock)
    }

    private data class FileBlocks(
        val firstBlock: List<String>,
        val secondBlock: List<String>,
    )
}
