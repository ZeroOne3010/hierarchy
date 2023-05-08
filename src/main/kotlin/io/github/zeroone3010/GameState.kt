package io.github.zeroone3010

import io.github.zeroone3010.MoveType.*
import io.github.zeroone3010.Side.BLUE
import io.github.zeroone3010.Side.RED

class GameState(
    private val board: Board,
    var turn: Side = BLUE,
    private val moveList: MutableList<Move> = mutableListOf()
) {

    fun legalMoves(): List<Move> {
        val searchAlgorithm = BreadthFirstSearch(board)
        val currentAttackByCurrentPlayer = getCurrentAttackBy(turn)
        val currentAttackByOpponent = getCurrentAttackBy(turn.other())
        if (currentAttackByCurrentPlayer != null) {
            val (from, to) = currentAttackByCurrentPlayer
            val fromSquare: Square = board.getSquare(from)
            val toSquare: Square? = searchAlgorithm.findReach(fromSquare)
                .attackReach
                .find { it.first.coordinates == to }
                ?.first
            if (toSquare?.piece?.owner == turn.other()) {
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
        for ((square, piece) in board.getPieces(turn)) {
            if (currentAttackByOpponent?.second?.equals(square.coordinates) == true) {
                // Piece under attack can not move, skip its moves
                continue
            }
            if (getExhaustedPieceSquare(turn)?.coordinates?.equals(square.coordinates) == true) {
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
                if (reachable.piece != null && reachable.piece!!.owner != turn) {
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

    fun move(move: Move) {
        var moveOk = false
        val source: Square = board.squares[move.from.x][move.from.y]
        val target: Square = board.squares[move.to.x][move.to.y]
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
            moveList.add(move)
            turn = turn.other()
        }
    }

    override fun toString(): String {
        val moves = moveList.mapIndexed { index, value ->
            "${index + 1}. $value"
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

        return "${sb}\nMoves: " + moves + "\nSide to move: ${turn.name}"
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
        return null
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