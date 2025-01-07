package org.fdesande

import java.time.Instant

fun main() {
    val problem = Day07()

    val start = Instant.now()
    val firstPart = problem.firstPart()

    val middle = Instant.now()

    val firstPartCalculationTime = middle.toEpochMilli() - start.toEpochMilli()
    println("The result of the first part is: $firstPart, calculated in $firstPartCalculationTime ms")

    val secondPart = problem.secondPart()
    val end = Instant.now()

    val secondPartCalculationTime = end.toEpochMilli() - middle.toEpochMilli()

    println("The result of the second part is: $secondPart, calculated in $secondPartCalculationTime ms")
}
