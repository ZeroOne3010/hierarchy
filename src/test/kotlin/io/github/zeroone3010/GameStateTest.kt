package io.github.zeroone3010

import io.github.zeroone3010.MoveType.ATTACK
import io.github.zeroone3010.MoveType.MOVE
import io.github.zeroone3010.Side.BLUE
import io.github.zeroone3010.Side.RED
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.beEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class GameStateTest : StringSpec() {
    init {
        "legalMoves should generate correct moves with 1-ranked pieces on Starting Area" {
            val gameState = GameState(
                Board.parseBoard(
                    """
          B6B1--------------
          B1----------------
          ------------------
          ------------------
          ------------------
          ------------------
          ------------------
          ------------------
          ----------------R6
        """.trimIndent()
                ),
                BLUE
            )
            val moves = gameState.legalMoves()

            moves shouldContainExactlyInAnyOrder listOf(
                Move(Coordinates(1, 0), Coordinates(2, 0), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(1, 0), Coordinates(3, 0), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(1, 0), Coordinates(1, 1), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(1, 0), Coordinates(2, 1), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(1, 0), Coordinates(1, 2), MOVE, Piece(BLUE, 1), null),

                Move(Coordinates(0, 1), Coordinates(0, 2), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(0, 1), Coordinates(0, 3), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(0, 1), Coordinates(1, 1), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(0, 1), Coordinates(1, 2), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(0, 1), Coordinates(2, 1), MOVE, Piece(BLUE, 1), null),
            )
        }

        "legalMoves should generate correct moves with 2-ranked pieces on Starting Area" {
            val gameState = GameState(
                Board.parseBoard(
                    """
          B6B2--------------
          B2----------------
          ------------------
          ------------------
          ------------------
          ------------------
          ------------------
          ------------------
          ----------------R6
        """.trimIndent()
                ),
                BLUE
            )
            val moves = gameState.legalMoves()

            moves shouldContainExactlyInAnyOrder listOf(
                Move(Coordinates(1, 0), Coordinates(2, 0), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(1, 0), Coordinates(3, 0), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(1, 0), Coordinates(1, 1), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(1, 0), Coordinates(2, 1), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(1, 0), Coordinates(1, 2), MOVE, Piece(BLUE, 2), null),

                Move(Coordinates(0, 1), Coordinates(0, 2), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(0, 1), Coordinates(0, 3), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(0, 1), Coordinates(1, 1), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(0, 1), Coordinates(1, 2), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(0, 1), Coordinates(2, 1), MOVE, Piece(BLUE, 2), null),
            )
        }

        "legalMoves should generate correct number of moves for Red 6 in the corner" {
            val gameState = GameState(
                Board.parseBoard(
                    """
          B6B2--------------
          B2----------------
          ------------------
          ------------------
          ------------------
          ------------------
          ------------------
          ------------------
          ----------------R6
        """.trimIndent()
                ),
                RED
            )
            val moves = gameState.legalMoves()

            moves.size shouldBe 27
        }

        "game should work with smaller board and legalMoves should generate correct moves for pieces outside of starting area, including attack moves" {
            val gameState = GameState(
                Board.parseBoard(
                    """
          B3R1--------
          R1----------
          ----------B2
          ----------B1
          ------------
          ----------R3
        """.trimIndent()
                ),
                BLUE
            )
            val moves = gameState.legalMoves()

            moves shouldContainExactlyInAnyOrder listOf(
                Move(Coordinates(5, 2), Coordinates(5, 0), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(5, 2), Coordinates(5, 1), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(5, 2), Coordinates(4, 1), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(5, 2), Coordinates(4, 2), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(5, 2), Coordinates(3, 2), MOVE, Piece(BLUE, 2), null),
                Move(Coordinates(5, 2), Coordinates(4, 3), MOVE, Piece(BLUE, 2), null),

                Move(Coordinates(5, 3), Coordinates(4, 3), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(5, 3), Coordinates(5, 4), MOVE, Piece(BLUE, 1), null),
                Move(Coordinates(5, 3), Coordinates(5, 5), ATTACK, Piece(BLUE, 1), Piece(RED, 3)),
            )
        }

        "legalMoves should return empty list when there are no legal moves for various reasons" {
            val gameState = GameState(
                Board.parseBoard(
                    """
          B3R1----R2R3
          R1----------
          ----------B1
          ------------
          ------------
          ------------
        """.trimIndent()
                ),
                BLUE,
                mutableListOf(Move(Coordinates(5, 0), Coordinates(5, 2), ATTACK, Piece(RED, 3), Piece(BLUE, 1)))
            )
            val moves = gameState.legalMoves()

            moves should beEmpty()
        }
    }
}
