package io.github.zeroone3010

data class Move(
    val from: Coordinates,
    val to: Coordinates,
    val type: MoveType,
    val piece: Piece,
    val targetPiece: Piece?
) {

    override fun toString(): String {
        val typeString = when (type) {
            MoveType.MOVE -> "-"
            MoveType.ATTACK -> "x"
            MoveType.DESTROY -> "X"
            MoveType.PASS -> "_"
        }
        return "${from.letterAndNumber()}$typeString${to.letterAndNumber()}"
    }

    companion object {
        val PASS_MOVE = Move(Coordinates(0, 0), Coordinates(0, 0), MoveType.PASS, Piece(Side.NONE, 0), null)

        fun parseMove(move: String?): Move {
            if (move == null) {
                return PASS_MOVE
            }
            val from = letterAndNumberToCoordinates(move.substring(0, 2))
            val type = when (move.substring(2, 3)) {
                "-" -> MoveType.MOVE
                "x" -> MoveType.ATTACK
                "X" -> MoveType.DESTROY
                else -> return PASS_MOVE
            }
            val to = letterAndNumberToCoordinates(move.substring(3, 5))
            println("Parsed: $from $type $to")
            return Move(from, to, type, Piece(Side.BLUE, 1), null)
        }

        private fun letterAndNumberToCoordinates(value: String): Coordinates {
            val row = letterToZeroBasedInt(value[0])
            val col = value[1].minus('1')
            return Coordinates(row, col)
        }
    }
}
