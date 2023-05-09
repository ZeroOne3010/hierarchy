package io.github.zeroone3010

import io.github.zeroone3010.MoveType.*
import io.github.zeroone3010.Side.*
import kotlin.math.roundToInt

private const val DRAW_AFTER_NUMBER_OF_NON_DESTROY_MOVES = 100

data class GameState(
    val board: Board,
    var turn: Side = BLUE,
    private val moveList: MutableList<Move> = mutableListOf()
) {

    fun legalMoves(side: Side = turn): List<Move> {
        val searchAlgorithm = BreadthFirstSearch(board)
        val currentAttackByCurrentPlayer = getCurrentAttackBy(side)
        val currentAttackByOpponent = getCurrentAttackBy(side.other())
        if (currentAttackByCurrentPlayer != null) {
            val (from, to) = currentAttackByCurrentPlayer
            val fromSquare: Square = board.getSquare(from)
            val toSquare: Square? = searchAlgorithm.findReach(fromSquare)
                .attackReach
                .find { it.first.coordinates == to }
                ?.first
            if (toSquare?.piece?.owner == side.other()) {
                return listOf(
                    Move(
                        fromSquare.coordinates,
                        toSquare.coordinates,
                        DESTROY,
                        fromSquare.piece!!,
                        toSquare.piece
                    )
                )
            }
        }

        val moves = mutableListOf<Move>()
        for ((square, piece) in board.getPieces(side)) {
            if (currentAttackByOpponent?.second?.equals(square.coordinates) == true) {
                // Piece under attack can not move, skip its moves
                continue
            }
            if (getExhaustedPieceSquare(side)?.coordinates?.equals(square.coordinates) == true) {
                // Skip moves of an exhausted piece
                continue
            }
            val reachableSquares = searchAlgorithm.findReach(square)
            for ((reachable, distance) in reachableSquares.moveReach) {
                if (reachable.piece == null && distance <= piece.maxMoveDistFrom(square)) {
                    moves.add(Move(square.coordinates, reachable.coordinates, MOVE, piece, null))
                }
            }
            for ((reachable, distance) in reachableSquares.attackReach) {
                if (reachable.piece != null && reachable.piece!!.owner != side) {
                    moves.add(Move(square.coordinates, reachable.coordinates, ATTACK, piece, reachable.piece))
                }
            }
        }
        return moves
    }

    private fun getExhaustedPieceSquare(side: Side?): Square? {
        val move = getPreviousMoveBy(side) ?: return null
        val fromSquare = board.getSquare(move.from)
        if (move.type == DESTROY) {
            return fromSquare;
        }
        if (move.type == ATTACK && side == turn) {
            // Need to check if this attack is still valid.
            // If it isn't, the piece is exhausted, but if it is, it's
            // forced to make the destroy move.
            val valid = BreadthFirstSearch(board)
                .findReach(fromSquare)
                .attackReach
                .map { it.first.coordinates }
                .contains(move.to)
            if (valid) {
                return null
            }
            return fromSquare
        }
        return null
    }

    private fun getCurrentAttackBy(side: Side?): Pair<Coordinates, Coordinates>? {
        val move = getPreviousMoveBy(side) ?: return null
        if (move.type == ATTACK) {
            return Pair(move.from, move.to)
        }
        return null
    }

    private fun getPreviousMoveBy(side: Side?): Move? {
        if (side == null) {
            return null
        }
        return when {
            side == turn && moveList.size > 1 -> moveList[moveList.size - 2]
            side != turn && moveList.size > 0 -> moveList[moveList.size - 1]
            else -> null
        }
    }

    fun move(move: Move): GameState {
        var moveOk = false
        val boardCopy: Board = board.clone()
        val source: Square = boardCopy.squares[move.from.x][move.from.y]
        val target: Square = boardCopy.squares[move.to.x][move.to.y]
        when (move.type) {
            MOVE -> {
                if (source.piece != null && target.piece == null) {
                    target.piece = source.piece
                    source.piece = null
                    moveOk = true
                } else {
                    println("Cannot make move $move")
                }
            }

            ATTACK -> {
                if (source.piece == null || target.piece == null || source.piece!!.owner == target.piece!!.owner) {
                    println("Cannot make attack move $move")
                } else {
                    moveOk = true
                }
            }

            DESTROY -> {
                if (source.piece == null || target.piece == null || source.piece!!.owner == target.piece!!.owner) {
                    println("Cannot make destroy move $move")
                } else {
                    target.piece = null
                    moveOk = true
                }
            }

            PASS -> {
                moveOk = true
            }
        }

        if (moveOk) {
            val moveListCopy = moveList.map { it.copy() }.toMutableList()
            moveListCopy.add(move)
            return this.copy(board = boardCopy, moveList = moveListCopy, turn = turn.other())
        }
        return this.copy()
    }

    override fun toString(): String {
        val moves = moveList.mapIndexed { index, value ->
            if (index % 2 == 0) {
                val moveNumber = ((index / 2.0) + 1.0).roundToInt()
                return@mapIndexed "$moveNumber. $value"
            }
            return@mapIndexed value.toString()
        }

        val sb = StringBuilder().append("   ")
        for (x in board.squares.indices) {
            sb.append(zeroBasedIntToLetter(x)).append("  ")
        }
        sb.append('\n')
        for (y in board.squares[0].indices) {
            sb.append(y + 1).append(" ")
            for (x in board.squares.indices) {
                sb.append(squareToString(board.squares[x][y])).append(' ')
            }
            sb.append('\n')
        }

        return """
               |${sb}
               |Moves until draw: ${movesUntilDraw()}
               |Moves: ${moves.joinToString(" ")}
               |Side to move: ${turn.name}
               """.trimMargin()
    }

    private fun squareToString(square: Square): String {
        when {
            square.hasExhaustedPiece() -> return ANSI_GREY_BACKGROUND + square.toString() + ANSI_RESET
            square.hasAttackingPiece() -> return ANSI_YELLOW_BACKGROUND + square.toString() + ANSI_RESET
            square.hasPieceUnderAttack() -> return ANSI_CYAN_BACKGROUND + square.toString() + ANSI_RESET
        }
        return square.toString();
    }

    fun getWinner(): Side? {
        val blueIsInTheGame = board.kingPieceExistsFor(BLUE)
        val redIsInTheGame = board.kingPieceExistsFor(RED)
        if (!blueIsInTheGame) {
            return RED
        }
        if (!redIsInTheGame) {
            return BLUE
        }
        if (movesUntilDraw() == 0) {
            return NONE
        }
        return null
    }

    fun movesUntilDraw(): Int {
        moveList.reversed().mapIndexed { index, move ->
            if (move.type == DESTROY) {
                return DRAW_AFTER_NUMBER_OF_NON_DESTROY_MOVES - index
            }
        }
        return DRAW_AFTER_NUMBER_OF_NON_DESTROY_MOVES
    }

    fun Square.hasExhaustedPiece(): Boolean {
        return equals(getExhaustedPieceSquare(piece?.owner))
    }

    fun Square.hasAttackingPiece(): Boolean {
        val currentAttack = getCurrentAttackBy(piece?.owner) ?: return false
        return equals(board.getSquare(currentAttack.first))
    }

    fun Square.hasPieceUnderAttack(): Boolean {
        val currentAttack = getCurrentAttackBy(piece?.owner?.other()) ?: return false
        return equals(board.getSquare(currentAttack.second))
    }
}
