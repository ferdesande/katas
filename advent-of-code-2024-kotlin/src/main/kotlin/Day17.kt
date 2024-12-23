package org.fdesande

import org.fdesande.common.FileUtils

class Day17 : AdventProblem {

    companion object {
        private const val INPUT = "/input17.txt"
    }

    override fun firstPart(): String {
        // Valid result: 2,0,7,3,0,3,1,3,7
        val initialState = getInitialState()
        return Computer(initialState).run()
    }

    override fun secondPart(): String {
        // Valid result: 247839539763386
        val initialState = getInitialState()
        var candidates = setOf(0L)
        while (candidates.isNotEmpty()) {
            val nextCandidates = candidates.flatMap { a ->
                (0..7)
                    .map { i -> (a shl 3) + i }
                    .map { newA -> Pair(newA, Computer(initialState).run(newA)) }
                    .map { pair -> Pair(pair.first, pair.second.split(",").map { it.toInt() }) }
                    .filter { pair -> pair.second == initialState.program.takeLast(pair.second.size) }
            }

            val result = nextCandidates
                .filter { pair -> pair.second == initialState.program }
                .minByOrNull { it.first }

            if (result != null) {
                return result.first.toString()
            }

            candidates = nextCandidates.map { it.first }.toSet()
        }
        return "Not found"
    }

    private fun getInitialState(): InitialState {
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
            .map { line -> line.split(": ")[1].trim() }

        return InitialState(
            a = lines[0].toLong(),
            b = lines[1].toLong(),
            c = lines[2].toLong(),
            program = lines[3].split(",").map { it.toInt() }
        )
    }

    private class Computer(private val initialState: InitialState) {
        fun run(newA: Long? = null): String {
            var registerA = newA ?: initialState.a
            var registerB = initialState.b
            var registerC = initialState.c
            val program = initialState.program

            var index = 0
            val output = mutableListOf<Long>()

            while (index < program.size) {
                val instruction = program[index]
                val comboValue = when (val value = program[index + 1]) {
                    4 -> registerA
                    5 -> registerB
                    6 -> registerC
                    else -> value.toLong()
                }
                when (instruction) {
                    0 -> registerA = registerA shr comboValue.toInt()           // adv
                    1 -> registerB = registerB xor program[index + 1].toLong()  // bxl
                    2 -> registerB = comboValue % 8                             // bst
                    3 -> if (registerA != 0L) {                                 // jnz
                        index = comboValue.toInt()
                        continue
                    }

                    4 -> registerB = registerB xor registerC                    // bxc
                    5 -> output.add(comboValue % 8)                             // out
                    6 -> registerB = registerA shr comboValue.toInt()           // bdv
                    7 -> registerC = registerA shr comboValue.toInt()           // cdv
                }
                index += 2
            }
            return output.joinToString(",")
        }
    }

    private data class InitialState(val a: Long, val b: Long, val c: Long, val program: List<Int>)
}
