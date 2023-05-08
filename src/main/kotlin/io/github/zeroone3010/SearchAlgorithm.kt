package io.github.zeroone3010

interface SearchAlgorithm {
    fun findRoute(start: Square, goal: Square): List<Square>
}

internal fun track(cameFrom: Map<Square, Square?>,
                                   start: Square,
                                   goal: Square): List<Square> {

    val track = mutableListOf<Square>()
    var current: Square = goal
    do {
        track.add(current)
        if (cameFrom[current] == null) {
            return emptyList()
        }
        current = cameFrom[current]!!
    } while (current != start)
    return track.toList()
}
