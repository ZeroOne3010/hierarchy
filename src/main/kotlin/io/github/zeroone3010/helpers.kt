package io.github.zeroone3010

const val ANSI_BLUE: String = "\u001B[34m"
const val ANSI_RESET: String = "\u001B[0m"
const val ANSI_RED: String = "\u001B[31m"
const val ANSI_GREY_BACKGROUND: String = "\u001b[47m"
const val ANSI_YELLOW_BACKGROUND: String = "\u001b[43m"
const val ANSI_CYAN_BACKGROUND: String = "\u001b[46m"

fun zeroBasedIntToLetter(x: Int): Char {
    return ('a'.code + x).toChar()
}
