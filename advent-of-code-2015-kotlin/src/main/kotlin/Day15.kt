package org.fdesande

import org.fdesande.common.FileUtils

class Day15 : AdventProblem {

    companion object {
        private const val INPUT = "/input15.txt"
        private val REGEX =
            Regex("""(\w+): capacity (-?\d+), durability (-?\d+), flavor (-?\d+), texture (-?\d+), calories (-?\d+)""")

        private const val CAPACITY = "capacity"
        private const val DURABILITY = "durability"
        private const val FLAVOR = "flavor"
        private const val TEXTURE = "texture"
        private const val CALORIES = "calories"
    }

    private val combinationGenerator = CombinationGenerator()

    override fun firstPart(): String {
        // Valid result: 21367368
        val ingredients = FileUtils.getLines(INPUT).mapNotNull { line -> parseIngredient(line) }

        return calculateRecipeScores(ingredients).map { a ->
            val values = a.filterNot { it.key == CALORIES }.values
            if (values.any { it < 0 })
                0
            else
                values.reduce { acc, i -> acc * i }
        }.sortedDescending().max().toString()
    }

    override fun secondPart(): String {
        // Valid result: 1766400
        val ingredients = FileUtils.getLines(INPUT).mapNotNull { line -> parseIngredient(line) }

        return calculateRecipeScores(ingredients).filter { it[CALORIES] == 500 }.map {
            val values = it.filterNot { e -> e.key == CALORIES }.values
            if (values.any { e -> e < 0 }) 0 else values.reduce { acc, i -> acc * i }
        }.sortedDescending().max().toString()
    }

    private fun calculateRecipeScores(ingredients: List<Ingredient>): List<Map<String, Int>> {
        return combinationGenerator.generate(ingredients.size).map { c ->
            ingredients.zip(c)
                .flatMap { it.first.getProperties(it.second) }
                .groupBy({ it.first }, { it.second })
                .mapValues { it.value.sum() }
        }
    }

    private fun parseIngredient(line: String): Ingredient? =
        REGEX.find(line)?.destructured?.let { (name, capacity, durability, flavor, texture, calories) ->
            Ingredient(name, capacity.toInt(), durability.toInt(), flavor.toInt(), texture.toInt(), calories.toInt())
        }

    private data class Ingredient(
        val name: String,
        val capacity: Int,
        val durability: Int,
        val flavor: Int,
        val texture: Int,
        val calories: Int,
    ) {
        fun getProperties(weight: Int): List<Pair<String, Int>> =
            listOf(
                CAPACITY to capacity * weight,
                DURABILITY to durability * weight,
                FLAVOR to flavor * weight,
                TEXTURE to texture * weight,
                CALORIES to calories * weight,
            )
    }

    private class CombinationGenerator {
        companion object {
            private const val SUM_RESULT = 100
        }

        fun generate(itemCount: Int): List<List<Int>> {
            val valid = mutableListOf<List<Int>>()

            (0..SUM_RESULT).forEach {
                generate(listOf(it), itemCount - 2, valid)
            }
            return valid
        }

        private fun generate(
            current: List<Int>,
            itemCount: Int,
            valid: MutableList<List<Int>>
        ) {
            val remaining = SUM_RESULT - current.sum()
            if (itemCount == 0) {
                valid.add(current + remaining)
            } else {
                (0..remaining).forEach {
                    generate(current + it, itemCount - 1, valid)
                }
            }
        }
    }
}
