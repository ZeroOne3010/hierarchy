package io.github.zeroone3010

typealias Row = Array<Square>

fun Array<Row>.copy() = Array(size) { get(it).copy() }
fun Array<Square>.copy() = Array(size) { get(it).copy() }

class Board(val squares: Array<Row>, val kingPieceValue: Int = 6) {

    fun clone(): Board {
        return Board(squares.copy(), kingPieceValue)
    }

    companion object {
        fun startingPosition(): Board {
            return parseBoard(
                """
            B6B5B4B3B2B1------
            B5B4B3B2B1--------
            B4B3B2B1----------
            B3B2B1----------R1
            B2B1----------R1R2
            B1----------R1R2R3
            ----------R1R2R3R4
            --------R1R2R3R4R5
            ------R1R2R3R4R5R6
        """.trimIndent()
            )
        }

        fun parseBoard(representation: String): Board {
            val rows = representation.trim().split('\n')
            val allSquares: List<List<String>> = rows.map { it.chunked(2) }
            val maxPieceValue = rows.size - 3

            val boardWidthAndHeight = rows.size
            return Board(squares = Array(boardWidthAndHeight) { x ->
                Array(boardWidthAndHeight) { y ->
                    val piece = Piece.parse(allSquares[y][x])
                    val squareOwner = determineSquareOwner(x, y, rows.size, maxPieceValue)
                    Square(coordinates = Coordinates(x, y), owner = squareOwner, piece = piece)
                }
            }, kingPieceValue = maxPieceValue)
        }

        private fun determineSquareOwner(x: Int, y: Int, rowSize: Int, maxPieceValue: Int): Side {
            val dimension = rowSize + 1
            val bluesOnThisRow = maxPieceValue - y
            val redsOnThisRowFrom = dimension - y
            return when {
                x < bluesOnThisRow -> Side.BLUE
                x > redsOnThisRowFrom -> Side.RED
                else -> Side.NONE
            }
        }
    }

    /**
     * <pre>
     * 0,0 ...  x,0
     *  .        .
     *  .        .
     * 0,y ...  x,y
     * </pre>
     */
    override fun toString(): String {
        val sb = StringBuilder().append("   ")
        for (x in squares.indices) {
            sb.append(zeroBasedIntToLetter(x)).append("  ")
        }
        sb.append('\n')
        for (y in squares[0].indices) {
            sb.append(y + 1).append(" ")
            for (x in squares.indices) {
                sb.append(squares[x][y]).append(' ')
            }
            sb.append('\n')
        }
        return sb.toString()
    }

    fun neighbors(square: Square): List<Square> {
        val x = square.coordinates.x
        val y = square.coordinates.y
        val result = mutableListOf<Square>()
        if (x - 1 in squares.indices) {
            result.add(squares[x - 1][y])
        }
        if (x + 1 in squares.indices) {
            result.add(squares[x + 1][y])
        }
        if (y - 1 in squares[x].indices) {
            result.add(squares[x][y - 1])
        }
        if (y + 1 in squares[x].indices) {
            result.add(squares[x][y + 1])
        }
        return result
    }

    fun getMaximumAttackDistance(square: Square, side: Side): Int {
        if (square.owner == side) {
            // Cannot attack from Starting Area
            return 0
        }
        return neighbors(square).sumOf { neighbor ->
            neighbor.piece?.let {
                when (it.owner) {
                    side -> it.rank
                    else -> -it.rank
                }
            } ?: 0
        }
    }

    fun getPieces(side: Side): List<Pair<Square, Piece>> {
        val pieces = mutableListOf<Pair<Square, Piece>>()
        for (y in squares[0].indices) {
            for (x in squares.indices) {
                if (squares[x][y].piece?.owner == side) {
                    pieces.add(Pair(squares[x][y], squares[x][y].piece!!))
                }
            }
        }
        return pieces
    }

    fun kingPieceExistsFor(side: Side): Boolean {
        return getPieces(side).map { it.second }.map { it.rank }.contains(kingPieceValue)
    }

    fun getSquare(coordinates: Coordinates): Square {
        return squares[coordinates.x][coordinates.y]
    }
}
