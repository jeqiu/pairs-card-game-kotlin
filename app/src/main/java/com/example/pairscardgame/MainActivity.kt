package com.example.pairscardgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pairscardgame.models.BoardSize
import com.example.pairscardgame.utils.DEFAULT_ICONS

class MainActivity : AppCompatActivity() {

    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumFlips: TextView
    private lateinit var tvNumPairs: TextView

    private var boardSize: BoardSize = BoardSize.MEDIUM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvBoard = findViewById(R.id.rvBoard)
        tvNumFlips = findViewById(R.id.tvNumFlips)
        tvNumPairs = findViewById(R.id.tvNumPairs)

        val chosenImages : List<Int> = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()

        rvBoard.adapter = CardBoardAdaptor(this, boardSize, randomizedImages)
        rvBoard.setHasFixedSize(true)
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }
}