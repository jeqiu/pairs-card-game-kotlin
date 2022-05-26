package com.example.pairscardgame

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pairscardgame.models.BoardSize
import com.example.pairscardgame.models.PairGame
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumFlips: TextView
    private lateinit var tvNumPairs: TextView
    private lateinit var pairGame: PairGame
    private lateinit var adaptor: BoardAdaptor
    private var boardSize: BoardSize = BoardSize.BEGINNER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvBoard = findViewById(R.id.rvBoard)
        tvNumFlips = findViewById(R.id.tvNumFlips)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        pairGame = PairGame(boardSize)

        adaptor = BoardAdaptor(this, boardSize, pairGame.cards, object : BoardAdaptor.CardClickListener{
            override fun onCardClick(position: Int) {
                updateGameOnFlip(position)
            }

        })
        rvBoard.adapter = adaptor
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    private fun updateGameOnFlip(position: Int) {
        // checking for error cases: all cards matched, and card already face-up
        if (pairGame.haveWonGame()) {
            Snackbar.make(clRoot, "Game complete! You already won!", Snackbar.LENGTH_LONG).show()
            return
        }
        if (pairGame.isCardFaceUp(position)) {
            Snackbar.make(clRoot, "Invalid move!", Snackbar.LENGTH_LONG).show()
            return
        }
        // actually flip the card
        if (pairGame.flipCard(position)) {
            Log.i(TAG, "Found a match. Num pairs found: ${pairGame.numPairsFound}")
        }
        adaptor.notifyDataSetChanged()
    }
}