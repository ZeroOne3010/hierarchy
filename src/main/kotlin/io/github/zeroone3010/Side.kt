package io.github.zeroone3010

enum class Side {
    RED,
    BLUE,
    NONE;

    override fun toString(): String {
        return when (this) {
            RED -> "R"
            BLUE -> "B"
            NONE -> "-"
        }
    }

    fun other(): Side {
        return when (this) {
            RED -> BLUE
            BLUE -> RED
            NONE -> NONE
        }
    }
}
