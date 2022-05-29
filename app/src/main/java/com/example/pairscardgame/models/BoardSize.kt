package com.example.pairscardgame.models

enum class BoardSize(val numCards : Int) {
    BEGINNER(8),
    EASY(12),
    MEDIUM(16),
    HARD(24);

    companion object {
        fun getByValue(value: Int) = values().first {it.numCards == value}
    }

    fun getWidth(): Int {
        return when (this) {
            BEGINNER -> 2
            EASY -> 3
            MEDIUM -> 4
            HARD -> 4
        }
    }

    fun getHeight(): Int {
        return numCards / getWidth()
    }

    fun getNumPairs(): Int {
        return numCards / 2
    }
}