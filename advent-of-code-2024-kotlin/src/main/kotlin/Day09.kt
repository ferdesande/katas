package org.fdesande

import org.fdesande.common.FileUtils

class Day09 : AdventProblem {

    companion object {
        private const val DOT = "."
        private const val INPUT = "/input09.txt"
    }

    override fun firstPart(): String {
        // Valid result: 6259790630969
        val input = FileUtils.getLines(INPUT).first()
        val expanded = expand(input.map { it })
        return compact(expanded)
            .filter { it != DOT }
            .mapIndexed { index, s -> index.toLong() * s.toLong() }
            .sum().toString()
    }

    override fun secondPart(): String {
        // Valid result: 6289564433984
        val input = FileUtils.getLines(INPUT).first()
        val diskSectors = extractSectors(input)
        rearrangeDiskSectors(diskSectors)
        // Arithmetic product
        return diskSectors.written
            .sumOf { block -> ((2 * block.position + block.length - 1) * block.length / 2).toLong() * block.id }
            .toString()
    }

    private fun expand(input: List<Char>): List<String> {
        var actualVale = 0
        var isFreeSpace = false
        val result = mutableListOf<String>()
        input
            .map { c -> c.digitToInt() }
            .forEach { i ->
                val string = if (isFreeSpace) DOT else actualVale++.toString()
                repeat(i) { result.add(string) }
                isFreeSpace = !isFreeSpace
            }
        return result
    }

    private fun compact(input: List<String>): List<String> {
        val result = input.toMutableList()
        var last = result.lastIndex
        for (i in result.indices) {
            if (result[i] == DOT) {
                while (result[last] == DOT && i < last) {
                    last--
                }
                result[i] = result[last]
                result[last] = DOT
            }
        }
        return result
    }

    private fun extractSectors(input: String): Sectors {
        val writtenBlocks = mutableListOf<Block>()
        val emptyBlocks = mutableListOf<Block>()
        var position = 0
        var id = 0
        input.forEachIndexed { index, c ->
            val value = c.digitToInt()
            val block = Block(
                position = position,
                length = value,
                id = id
            )
            position += value
            if (index % 2 == 0) {
                writtenBlocks.add(block)
                id++
            } else {
                emptyBlocks.add(block)
            }
        }
        writtenBlocks.removeIf { it.length == 0 }
        emptyBlocks.removeIf { it.length == 0 }
        return Sectors(writtenBlocks, emptyBlocks)
    }

    private fun rearrangeDiskSectors(diskSectors: Sectors) {
        val written = diskSectors.written
        val empty = diskSectors.empty

        for (i in written.indices.reversed()) {
            val block = written[i]

            if (block.position < empty.first().position) {
                return
            }

            val emptyBlockIndex = empty
                .indexOfFirst { emptyBlock -> emptyBlock.position < block.position && block.length <= emptyBlock.length }
            if (emptyBlockIndex != -1) {
                val emptyBlock = empty[emptyBlockIndex]
                val newBlock = Block(emptyBlock.position, block.length, block.id)
                val newEmptyBlock = Block(emptyBlock.position + block.length, emptyBlock.length - block.length, -1)
                written[i] = newBlock
                if (newEmptyBlock.length == 0) {
                    empty.removeAt(emptyBlockIndex)
                } else {
                    empty[emptyBlockIndex] = newEmptyBlock
                }
            }
        }
    }

    private data class Sectors(val written: MutableList<Block>, val empty: MutableList<Block>)
    private data class Block(val position: Int, val length: Int, val id: Int)
}
