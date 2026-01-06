package org.fdesande

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.fdesande.common.FileUtils

class Day12 : AdventProblem {

    companion object {
        private const val INPUT = "/input12.txt"
        private val objectMapper = jacksonObjectMapper()
    }

    override fun firstPart(): String {
        val json = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        val matches = Regex("""-?(\d+)""").findAll(json)
            .map { it.groupValues[0] }
            .map { it.toInt() }
            .sum()

        return matches.toString()
    }

    override fun secondPart(): String {
        val json = FileUtils.getLines(INPUT).single { it.isNotBlank() }
        val root = objectMapper.readTree(json) ?: return "Invalid json"
        val nodeValues = root.map { getNodeValue(it) }
        return nodeValues.sum().toString()
    }

    private fun getNodeValue(node: JsonNode): Int {
        return when {
            node.isObject ->
                if (containsRed(node)) 0
                else node.sumOf { getNodeValue(it) }

            node.isArray -> node.sumOf { getNodeValue(it) }
            node.isInt -> node.intValue()
            else -> return 0
        }
    }

    private fun containsRed(node: JsonNode): Boolean = node.any { it.isTextual && it.textValue() == "red" }
}
