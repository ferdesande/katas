package org.fdesande

import org.fdesande.common.FileUtils
import org.fdesande.common.Grid
import org.fdesande.common.GridBounds
import org.fdesande.common.Point

class Day06 : AdventProblem {

    companion object {
        private const val INPUT = "/input06.txt"
        private const val LIGHT_OFF = false
        private const val LIGHT_ON = true
        private val REGEX = Regex("(\\w+) (\\d+),(\\d+) through (\\d+),(\\d+)")
    }

    override fun firstPart(): String {
        val grid = Grid.create(1000, 1000, LIGHT_OFF)
        getInstructions().forEach { instruction ->
            (instruction.bounds.minX..instruction.bounds.maxX)
                .forEach { x ->
                    (instruction.bounds.minY..instruction.bounds.maxY)
                        .forEach { y ->
                            val point = Point(x, y)
                            when (instruction.command) {
                                Command.TURN_ON -> grid.turnOn(point)
                                Command.TURN_OFF -> grid.turnOff(point)
                                Command.TOGGLE -> grid.toggle(point)
                            }
                        }
                }
        }
        return grid.getContent().count { it.second == LIGHT_ON }.toString()
    }

    override fun secondPart(): String {
        val grid = Grid.create(1000, 1000, 0)
        getInstructions().forEach { instruction ->
            (instruction.bounds.minX..instruction.bounds.maxX)
                .forEach { x ->
                    (instruction.bounds.minY..instruction.bounds.maxY)
                        .forEach { y -> grid.adaptBrightness(Point(x, y), instruction.command) }
                }
        }
        return grid.getContent().sumOf { it.second }.toString()
    }

    private fun Grid<Boolean>.turnOn(point: Point) {
        setValue(point, LIGHT_ON)
    }

    private fun Grid<Int>.adaptBrightness(point: Point, command: Command) {
        val increase = when (command) {
            Command.TURN_ON -> 1
            Command.TURN_OFF -> -1
            Command.TOGGLE -> 2
        }
        val updatedValue = getValue(point)!! + increase
        if (updatedValue > 0)
            setValue(point, updatedValue)
        else
            setValue(point, 0)
    }

    private fun Grid<Boolean>.turnOff(point: Point) {
        setValue(point, LIGHT_OFF)
    }

    private fun Grid<Boolean>.toggle(point: Point) {
        if (getValue(point) == LIGHT_ON)
            setValue(point, LIGHT_OFF)
        else
            setValue(point, LIGHT_ON)
    }

    private fun getInstructions() = FileUtils.getLines(INPUT)
        .filter { it.isNotBlank() }
        .map { line ->
            val match = REGEX.find(line)!!
            val command = when (match.groupValues[1]) {
                "on" -> Command.TURN_ON
                "off" -> Command.TURN_OFF
                "toggle" -> Command.TOGGLE
                else -> throw IllegalArgumentException("Unrecognised command: $match")
            }
            val bounds = GridBounds(
                minX = match.groupValues[2].toInt(),
                minY = match.groupValues[3].toInt(),
                maxX = match.groupValues[4].toInt(),
                maxY = match.groupValues[5].toInt(),
            )
            Instruction(command, bounds)
        }

    private enum class Command { TURN_ON, TURN_OFF, TOGGLE }
    private data class Instruction(val command: Command, val bounds: GridBounds)
}
