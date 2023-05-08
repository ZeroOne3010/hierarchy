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
    }
}
