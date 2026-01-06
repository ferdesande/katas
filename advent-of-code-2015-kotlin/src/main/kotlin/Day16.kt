package org.fdesande

import org.fdesande.common.FileUtils

class Day16 : AdventProblem {

    companion object {
        private const val INPUT = "/input16.txt"
        private val AUNT_REGEX = Regex("""Sue (\w+): (.+)""")
        private val ITEM_REGEX = Regex("""(\w+): (\d+)""")

        private const val CHILDREN = "children"
        private const val CATS = "cats"
        private const val SAMOYEDS = "samoyeds"
        private const val POMERANIANS = "pomeranians"
        private const val AKITAS = "akitas"
        private const val VIZSLAS = "vizslas"
        private const val GOLDFISH = "goldfish"
        private const val TREES = "trees"
        private const val CARS = "cars"
        private const val PERFUMES = "perfumes"

        private val AUNT_STUFF: Map<String, Int> = mapOf(
            CHILDREN to 3,
            CATS to 7,
            SAMOYEDS to 2,
            POMERANIANS to 3,
            AKITAS to 0,
            VIZSLAS to 0,
            GOLDFISH to 5,
            TREES to 3,
            CARS to 2,
            PERFUMES to 1,
        )
    }

    override fun firstPart(): String {
        return FileUtils.getLines(INPUT)
            .mapNotNull { line -> parseAunt(line) }.toMap()
            .filter { entry -> entry.value.all { item -> AUNT_STUFF[item.key] == item.value } }
            .map { it.key }.single().toString()
    }

    override fun secondPart(): String {
        return FileUtils.getLines(INPUT)
            .mapNotNull { line -> parseAunt(line) }.toMap()
            .filter { entry ->
                entry.value.all { item ->
                    when (item.key) {
                        CATS, TREES -> AUNT_STUFF[item.key]!! < item.value
                        POMERANIANS, GOLDFISH -> AUNT_STUFF[item.key]!! > item.value
                        else -> AUNT_STUFF[item.key] == item.value
                    }
                }
            }
            .map { it.key }.single().toString()
    }

    private fun parseAunt(line: String): Pair<Int, Map<String, Int>>? {
        return AUNT_REGEX.find(line)?.destructured?.let { (number, values) ->
            val items = values.split(",").mapNotNull { value ->
                ITEM_REGEX.find(value)?.destructured?.let { (id, quantity) -> id to quantity.toInt() }
            }.toMap()
            number.toInt() to items
        }
    }
}
