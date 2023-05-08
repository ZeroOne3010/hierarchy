package io.github.zeroone3010

import java.util.PriorityQueue

data class PrioritizedSquare(val square: Square, val priority: Int)

class AStar(private val board: Board) : SearchAlgorithm {
    private val staticCostOfMoving = 1

    override fun findRoute(start: Square, goal: Square): List<Square> {
        // Based on the example here: https://www.redblobgames.com/pathfinding/a-star/introduction.html
        val frontier = PriorityQueue<PrioritizedSquare>(compareBy { it.priority })
        frontier.add(PrioritizedSquare(start, 0))
        val cameFrom = mutableMapOf<Square, Square?>()
        val costSoFar = mutableMapOf<Square, Int>()
        cameFrom[start] = null
        costSoFar[start] = 0

        while (frontier.isNotEmpty()) {
            val current = frontier.poll()
            if (current.square === goal) {
                break
            }

            for (next in board.neighbors(current.square)) {
                val newCost = (costSoFar[current.square] ?: 0) + staticCostOfMoving
                if (next !in costSoFar || newCost < (costSoFar[next] ?: Int.MAX_VALUE)) {
                    costSoFar[next] = newCost
                    val priority = newCost + manhattanDistance(goal, next)
                    frontier.add(PrioritizedSquare(next, priority))
                    cameFrom[next] = current.square
                }
            }
        }

        return track(cameFrom, start, goal)
    }

    private fun manhattanDistance(a: Square, b: Square): Int {
        val diff = (a.coordinates - b.coordinates).abs()
        return diff.x + diff.y
    }
}
