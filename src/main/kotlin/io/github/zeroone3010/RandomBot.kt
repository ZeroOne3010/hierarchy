package io.github.zeroone3010

/**
 * Makes completely random moves.
 */
class RandomBot : Player {
    override fun move(state: GameState): Move {
        val legalMoves = state.legalMoves()
        return legalMoves.randomOrNull() ?: Move.PASS_MOVE
    }

    override fun name(): String {
        return "RandomBot"
    }
}
