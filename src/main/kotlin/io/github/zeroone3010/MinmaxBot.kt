package io.github.zeroone3010

import io.github.zeroone3010.Side.*

private const val MAX_DEPTH = 3

/**
 * A bot that will recursively evaluate the possible moves with a simple algorithm and will make the move
 * it considers the best according to its evaluation function.
 */
class MinmaxBot(private val mySide: Side) : Player {

    override fun move(state: GameState): Move {
        val (score, move) = minmax(state, mySide, MAX_DEPTH)
        print("${ANSI_BLUE}${name()} here! I evaluated $move to be the best with the score of $score")
        when {
            score > 0 -> println(", i.e. better for BLUE.$ANSI_RESET")
            score < 0 -> println(", i.e. better for RED.$ANSI_RESET")
            else -> println(", i.e. equal.$ANSI_RESET")
        }
        return move!!
    }

    private fun minmax(state: GameState, side: Side, depth: Int): Pair<Int, Move?> {
        if (depth == 0 || state.getWinner() != null) {
            return Pair(evaluate(state), null)
        }

        val legalMoves = state.legalMoves()

        if (legalMoves.isEmpty()) {
            return Pair(evaluate(state), Move.PASS_MOVE)
        }

        var bestMove: Move? = null
        var bestScore: Int = if (side == BLUE) Int.MIN_VALUE else Int.MAX_VALUE

        for (move in legalMoves) {
            val newState = state.move(move)
            val newDepth = if (move.type == MoveType.DESTROY) depth else depth - 1
            val (score, _) = minmax(newState, side.other(), newDepth)

            if (side == BLUE) {
                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }
            } else {
                if (score < bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }
        }

        return Pair(bestScore, bestMove)
    }

    private fun evaluate(state: GameState): Int {
        if (state.getWinner() == BLUE) {
            return Int.MAX_VALUE - 1
        } else if (state.getWinner() == RED) {
            return Int.MIN_VALUE + 1
        } else if (state.getWinner() == NONE) {
            return 0
        }
        var situationValue = 0

        val redPieces = state.board.getPieces(RED)
        val bluePieces = state.board.getPieces(BLUE)

        val blueFirepower = getTotalFirepower(state, BLUE)
        val redFirepower = getTotalFirepower(state, RED)

        situationValue += (bluePieces.size - redPieces.size) * 10
        situationValue += (blueFirepower - redFirepower)

        return situationValue
    }

    private fun getTotalFirepower(state: GameState, side: Side) = state.board.getPieces(side)
        .filter { it.second.owner == side }
        .sumOf { state.board.getMaximumAttackDistance(it.first, side) }

    override fun name(): String {
        return "MinmaxBot"
    }
}
