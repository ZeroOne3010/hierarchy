package io.github.zeroone3010

/**
 * Just like RandomBot, but will make an attacking move if possible, and will attack the highest ranked enemy piece.
 */
class GreedyBot : Player {

    private val greedyAttackMoveComparator = Comparator<Move> { move1, move2 ->
        when {
            move1.type == MoveType.ATTACK && move2.type != MoveType.ATTACK -> -1
            move1.type != MoveType.ATTACK && move2.type == MoveType.ATTACK -> 1
            move1.type == MoveType.ATTACK && move2.type == MoveType.ATTACK -> {
                // Sort by targetPiece rank
                move2.targetPiece!!.rank - move1.targetPiece!!.rank
            }

            else -> 0
        }
    }

    override fun move(state: GameState): Move {
        val legalMoves = state.legalMoves()
        if (legalMoves.find { it.type == MoveType.ATTACK } != null) {
            return legalMoves.sortedWith(greedyAttackMoveComparator)[0]
        }
        return legalMoves.random()
    }

    override fun name(): String {
        return "GreedyBot"
    }
}
