package com.example.pairscardgame.models

data class PairCard(
    val identifier: Int,
    val imageUrl: String? = null,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false
)