package org.fdesande

import org.fdesande.common.FileUtils

class Day23 : AdventProblem {

    companion object {
        private const val INPUT = "/input23.txt"
    }

    override fun firstPart(): String {
        // Valid result: 1512
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val connectionsByNode = getConnectionsByNode(lines)
        return getThreeNodeLinks(connectionsByNode)
            .count { it.any { node -> node.startsWith("t") } }
            .toString()
    }

    override fun secondPart(): String {
        // Valid result: ac,ed,fh,kd,lf,mb,om,pe,qt,uo,uy,vr,wg
        val lines = FileUtils.getLines(INPUT).filter { it.isNotBlank() }
        val solver = BronKerboschAlgorithm(getConnectionsByNode(lines))
        return solver.solve()
    }

    private fun getConnectionsByNode(lines: List<String>) = lines
        .map { line -> line.split("-") }
        .flatMap { listOf(Pair(it[0], it[1]), Pair(it[1], it[0])) }
        .groupBy({ it.first }, { it.second })
        .mapValues { it.value.toSet() }

    private fun getThreeNodeLinks(connectionsByNode: Map<String, Set<String>>) =
        connectionsByNode
            .flatMap { entry ->
                entry.value.flatMap { firstNode ->
                    connectionsByNode[firstNode]!!.flatMap { secondNode ->
                        connectionsByNode[secondNode]!!.map { thirdNode ->
                            if (firstNode in connectionsByNode[thirdNode]!!) {
                                setOf(firstNode, secondNode, thirdNode)
                            } else {
                                emptySet()
                            }
                        }
                    }
                }
            }
            .filter { it.size == 3 }
            .map { it.sorted() }
            .toSet()

    // Hint: The Bron-Kerbosch algorithm is a well-known algorithm to find the larges clique in a graph.
    //       A clique is a complete subset of connected vertex.
    //       The algorithm was found at: https://rosettacode.org/wiki/Bron%E2%80%93Kerbosch_algorithm#Java
    //       To understand the algorithm, take a look to: https://www.youtube.com/watch?v=j_uQChgo72I
    private class BronKerboschAlgorithm(private val graph: Map<String, Set<String>>) {
        private var cliques = mutableListOf<List<String>>()

        fun solve(): String {
            val currentClique: MutableSet<String> = mutableSetOf()
            val candidates: MutableSet<String> = graph.keys.sorted().toSet().toMutableSet()
            val processedVertices: MutableSet<String> = mutableSetOf()

            iterate(currentClique, candidates, processedVertices)

            return cliques.maxBy { it.size }.joinToString(",")
        }

        private fun iterate(
            currentClique: MutableSet<String>,
            candidates: MutableSet<String>,
            processedVertices: MutableSet<String>
        ) {
            if (candidates.isEmpty() && processedVertices.isEmpty()) {
                if (currentClique.size > 2) {
                    cliques.add(currentClique.sorted())
                }
                return
            }

            // Select a pivot vertex from 'candidates' union 'processedVertices' with the maximum degree
            val union: MutableSet<String> = HashSet(candidates)
            union.addAll(processedVertices)
            val pivot: String = graph.entries
                .filter { entry -> entry.key in union }
                .maxBy { entry -> entry.value.size }
                .key

            // 'possibles' are vertices in 'candidates' that are not neighbours of the 'pivot'
            val possibles: MutableSet<String> = HashSet(candidates)
            possibles.removeAll(graph[pivot]!!)

            for (vertex in possibles) {
                // Create a new clique including 'vertex'
                val newCliques: MutableSet<String> = HashSet(currentClique)
                newCliques.add(vertex)

                // 'newCandidates' are the members of 'candidates' that are neighbours of 'vertex'
                val neighbours = graph[vertex]
                val newCandidates: MutableSet<String> = HashSet(candidates)
                newCandidates.retainAll(neighbours!!)

                // 'newProcessedVertices' are members of 'processedVertices' that are neighbours of 'vertex'
                val newProcessedVertices: MutableSet<String> = HashSet(processedVertices)
                newProcessedVertices.retainAll(neighbours)

                // Recursive call with the updated sets
                iterate(newCliques, newCandidates, newProcessedVertices)

                // Move 'vertex' from 'candidates' to 'processedVertices'
                candidates.remove(vertex)
                processedVertices.add(vertex)
            }
        }
    }
}
