package io.github.zeroone3010

/**
 * Lets a human select the moves.
 */
class Human : Player {
    override fun move(state: GameState): Move {
        print("Enter a move: ")
        val input = readLine()
        return Move.parseMove(input)
    }

    override fun name(): String {
        return "Human"
    }
}
