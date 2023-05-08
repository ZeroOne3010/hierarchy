package io.github.zeroone3010

data class Piece(val owner: Side, val rank: Int) {
    override fun toString(): String {
        val color = when (owner) {
            Side.BLUE -> ANSI_BLUE
            else -> ANSI_RED
        }
        return color + owner.toString() + rank + ANSI_RESET
    }

    fun maxMoveDistFrom(square: Square): Int {
        if (square.owner == owner && rank == 1) {
            return 2
        }
        return rank
    }

    companion object {
        fun parse(stringRepresentation: String): Piece? {
            val side = when {
                stringRepresentation[0] == 'R' -> Side.RED
                stringRepresentation[0] == 'B' -> Side.BLUE
                else -> return null
            }
            val rank = stringRepresentation[1].digitToInt()
            return Piece(side, rank);
        }
    }
}
