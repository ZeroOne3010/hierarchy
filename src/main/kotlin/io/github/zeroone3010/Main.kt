package io.github.zeroone3010

import io.github.zeroone3010.Side.BLUE
import io.github.zeroone3010.Side.RED

fun main() {
    var game = GameState(Board.startingPosition(), BLUE)
    //val game = GameState(
    //    Board.parseBoard(
    //        """
    //  ------------------
    //  --------B4--------
    //  --------------R1--
    //  --B6--------R1R2--
    //  ----------R3R2R1--
    //  --------R1------R3
    //  ------R2----R2R4R4
    //  ------R1R2--R3--R5
    //  --R1------R3R4R5R6
    //""".trimIndent()
    //    ),
    //    RED
    //    //mutableListOf(Move(Coordinates(5, 5), Coordinates(4, 3),
    //    //    MoveType.ATTACK, Piece(RED, 1), Piece(BLUE, 1)))
    //)

    val players = mapOf(BLUE to MinmaxBot(BLUE), RED to GreedyBot())

    println("Initial state:\n" + game)

    while (game.getWinner() == null) {
        val moves: List<Move> = game.legalMoves()
        println("\nLegal moves: " + moves.sortedBy { it.toString() })

//        print("Press enter to see a move by ${players[game.turn]!!.name()} (${game.turn.name})...")
//        readLine()

        val move = players[game.turn]!!.move(game)
        game = game.move(move)

        println("\nMove is $move, the situation now looks like this:\n" + game)
    }

    println("${players[game.getWinner()]!!.name()} (${game.getWinner()?.name}) wins!")
}


