package org.fdesande

import org.fdesande.common.FileUtils

class Day14 : AdventProblem {

    companion object {
        private const val INPUT = "/input14.txt"
        private val REGEX =
            Regex("""(\w+) can fly (\d+) km/s for (\d+) seconds, but then must rest for (\d+) seconds.""")
    }

    override fun firstPart(): String {
        // Valid result: 2696
        val herd = FileUtils.getLines(INPUT).mapNotNull { line -> parseDeer(line) }
        val positionByReindeer = herd.associateWith { reindeer -> reindeer.positionAfter(2503) }

        return positionByReindeer.maxOf { it.value }.toString()
    }

    override fun secondPart(): String {
        // Valid result: 1084
        val herd = FileUtils.getLines(INPUT).mapNotNull { line -> parseDeer(line) }
        val positionByTimeByReindeer = (1..2503).associateWith { time ->
            herd.associateWith { reindeer -> reindeer.positionAfter(time) }
        }

        val leadDistanceByTime = positionByTimeByReindeer.entries
            .associate { e -> e.key to e.value.maxOf { it.value } }
        val leadByReindeer = leadDistanceByTime.entries.flatMap { e ->
            positionByTimeByReindeer[e.key]!!.filter { it.value == e.value }.map { it.key }
        }.groupingBy { it }.eachCount()

        return leadByReindeer.maxOf { it.value }.toString()
    }

    private fun parseDeer(line: String): Reindeer? =
        REGEX.find(line)?.destructured?.let { (name, speed, flyingTime, restTime) ->
            Reindeer(name, speed.toInt(), flyingTime.toInt(), restTime.toInt())
        }

    private data class Reindeer(val name: String, val speed: Int, val flyingTime: Int, val restTime: Int) {
        fun positionAfter(seconds: Int): Int {
            val cycleTime = flyingTime + restTime
            val cycles = seconds / cycleTime
            val remainingTime = minOf(flyingTime, seconds % cycleTime)

            return (flyingTime * cycles + remainingTime) * speed
        }
    }
}
