package io.github.zeroone3010

interface Player {
    fun move(state : GameState) : Move

    fun name() : String
}
