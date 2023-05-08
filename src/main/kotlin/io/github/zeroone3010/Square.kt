package io.github.zeroone3010

data class Square(
    val coordinates: Coordinates,
    val owner: Side,
    var piece: Piece?
) {
    override fun toString(): String {
        return when {
            piece != null -> piece.toString()
            owner == Side.BLUE -> "$ANSI_BLUE--$ANSI_RESET"
            owner == Side.RED -> "$ANSI_RED--$ANSI_RESET"
            else -> "--"
        }
    }
}
