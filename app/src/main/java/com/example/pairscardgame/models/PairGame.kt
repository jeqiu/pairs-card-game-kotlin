package com.example.pairscardgame.models

import com.example.pairscardgame.utils.DEFAULT_IMAGES

class PairGame(
    private val boardSize: BoardSize,
    private val customImages: List<String>?,
) {

    val cards: List<PairCard>
    var numPairsFound = 0

    private var numCardFlips = 0
    private var indexOfSingleFlippedCard: Int? = null

    init {
        if (customImages == null) {
            val chosenImages : List<Int> = DEFAULT_IMAGES.shuffled().take(boardSize.getNumPairs())
            val randomizedImages = (chosenImages + chosenImages).shuffled()
            cards = randomizedImages.map{ PairCard(it)}
        } else {
            val randomizedImages = (customImages + customImages).shuffled()
            cards = randomizedImages.map { PairCard(it.hashCode(), it) }
        }

    }

    fun flipCard(position: Int): Boolean {
        numCardFlips++
        val card = cards[position]
        // 0 cards flipped over -> selected card is flipped over
        // 1 card already flipped over -> flip selected card over & check if match
        // 2 cards already flipped over -> restore & flip selected card over
        var foundMatch = false
        if (indexOfSingleFlippedCard == null) {
            // 0 or 2 cards already flipped over
            restoreCards()
            indexOfSingleFlippedCard = position
        } else {
            // 1 card already flipped over
            foundMatch = checkForMatch(indexOfSingleFlippedCard!!, position)
            indexOfSingleFlippedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if (cards[position1].identifier != cards[position2].identifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumFlips(): Int {
        return numCardFlips
    }
}
