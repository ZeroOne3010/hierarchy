package io.github.zeroone3010

data class Coordinates(
    val x: Int,
    val y: Int
) {
    operator fun minus(other: Coordinates): Coordinates {
        return Coordinates(this.x - other.x, this.y - other.y)
    }

    fun abs(): Coordinates {
        return Coordinates(kotlin.math.abs(this.x), kotlin.math.abs(this.y))
    }

    override fun toString(): String {
        return "(${x}, ${y})"
    }

    fun letterAndNumber(): String {
        return zeroBasedIntToLetter(x).toString() + (y + 1)
    }
}
