package io.github.zeroone3010

import java.util.*

data class Reach(
    val moveReach: List<Pair<Square, Int>>,
    val attackReach: List<Pair<Square, Int>>
)

class BreadthFirstSearch(private val board: Board) : SearchAlgorithm {

    override fun findRoute(start: Square, goal: Square): List<Square> {
        // Based on the example here: https://www.redblobgames.com/pathfinding/a-star/introduction.html
        val frontier: Queue<Square> = LinkedList()
        frontier.add(start)
        val cameFrom = mutableMapOf<Square, Square?>()
        cameFrom[start] = null

        while (frontier.isNotEmpty()) {
            val current = frontier.poll()
            if (current === goal) {
                break
            }

            for (next in board.neighbors(current)) {
                if (next !in cameFrom) {
                    frontier.add(next)
                    cameFrom[next] = current
                }
            }
        }

        return track(cameFrom, start, goal)
    }

    fun findReach(start: Square): Reach {
        if (start.piece == null) {
            return Reach(listOf(), listOf())
        }
        val piece = start.piece!!
        var maxMoveDistance = piece.rank
        val maxAttackDistance = board.getMaximumAttackDistance(start, piece.owner)
        if (maxMoveDistance == 1 && start.owner == piece.owner) {
            maxMoveDistance = 2
        }
        val maxDistance = intArrayOf(maxMoveDistance, maxAttackDistance).max()
        val frontier: Queue<Pair<Square, Int>> = LinkedList()
        frontier.add(Pair(start, 0))
        val visited = mutableSetOf(start)
        val moveReach = mutableListOf<Pair<Square, Int>>()
        val attackReach = mutableListOf<Pair<Square, Int>>()

        while (frontier.isNotEmpty()) {
            val (current, distance) = frontier.poll()

            for (next in board.neighbors(current)) {
                val nextDistance = distance + 1
                if (next !in visited && nextDistance <= maxDistance) {
                    visited.add(next)
                    val reach = Pair(next, nextDistance)
                    if (reach.first.piece == null) {
                        frontier.add(reach)
                    }
                    if (nextDistance <= maxMoveDistance) {
                        moveReach.add(reach)
                    }
                    if (nextDistance <= maxAttackDistance) {
                        attackReach.add(reach)
                    }
                }
            }
        }

        return Reach(moveReach, attackReach)
    }
}
