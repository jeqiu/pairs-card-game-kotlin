package com.example.pairscardgame

import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pairscardgame.models.BoardSize
import com.example.pairscardgame.models.PairGame
import com.example.pairscardgame.models.UserImageList
import com.example.pairscardgame.utils.EXTRA_BOARD_SIZE
import com.example.pairscardgame.utils.EXTRA_GAME_NAME
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val CREATE_REQUEST_CODE = 778
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumFlips: TextView
    private lateinit var tvNumPairs: TextView

    private val db = Firebase.firestore
    private var gameName: String? = null
    private var customGameImages: List<String>? = null
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

        setupBoard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                // setup game again
                if (pairGame.getNumFlips() > 0 && !pairGame.haveWonGame()) {
                    showAlert("Quit your current game?", null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    setupBoard()
                }
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
            R.id.mi_custom_game -> {
                showCreationDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val customGameName = data?.getStringExtra(EXTRA_GAME_NAME)
            if (customGameName == null) {
                Log.e(TAG, "Got null custom game name from CreateActivity")
                return
            }
            downloadGame(customGameName)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun downloadGame(customGameName: String) {
        db.collection("matchingPairGames").document(customGameName).get()
            .addOnSuccessListener { document ->
                val userImageList = document.toObject(UserImageList::class.java)
                if (userImageList?.images == null) {
                    Log.e(TAG, "Invalid custom game data from Firestore")
                    Snackbar.make(clRoot, "No such game found, '$customGameName'", Snackbar.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }
                val numCards = userImageList.images.size * 2
                boardSize = BoardSize.getByValue(numCards)
                customGameImages = userImageList.images
                setupBoard()
                gameName = customGameName

            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Exception when retrieving game", exception)
            }
    }

    private fun showCreationDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        showAlert("Create a custom game with your own images", boardSizeView, View.OnClickListener {

            val desiredBoardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbBeginner -> BoardSize.BEGINNER
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            // navigate to a new activity to choose images
            val intent = Intent(this, CreateActivity::class.java)
            intent.putExtra(EXTRA_BOARD_SIZE, desiredBoardSize)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    private fun showNewSizeDialog() {
        val boardSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize) {
            BoardSize.BEGINNER -> radioGroupSize.check(R.id.rbBeginner)
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
        }
        showAlert("Choose new difficulty", boardSizeView, View.OnClickListener {
            // set new board size value
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbBeginner -> BoardSize.BEGINNER
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                else -> BoardSize.HARD
            }
            gameName = null
            customGameImages = null
            setupBoard()
        })
    }

    private fun showAlert(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") {_, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun setupBoard() {
        supportActionBar?.title = gameName ?: getString(R.string.app_name)
        when (boardSize) {
            BoardSize.BEGINNER -> {
                tvNumFlips.text = "Beginner: 4 x 2"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.EASY -> {
                tvNumFlips.text = "Easy: 4 x 3"
                tvNumPairs.text = "Pairs: 0 / 6"
            }
            BoardSize.MEDIUM -> {
                tvNumFlips.text = "Medium: 4 x 4"
                tvNumPairs.text = "Pairs: 0 / 8"
            }
            BoardSize.HARD -> {
                tvNumFlips.text = "Hard: 6 x 4"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }
        tvNumPairs.setTextColor((ContextCompat.getColor(this, R.color.color_progress_none)))
        pairGame = PairGame(boardSize, customGameImages)

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
            Snackbar.make(clRoot, "Invalid move!", Snackbar.LENGTH_SHORT).show()
            return
        }
        // actually flip the card
        if (pairGame.flipCard(position)) {
            Log.i(TAG, "Found a match. Pairs found: ${pairGame.numPairsFound} / ${boardSize.getNumPairs()}")
            val color = ArgbEvaluator().evaluate(
                pairGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                ContextCompat.getColor(this, R.color.color_progress_none),
                ContextCompat.getColor(this, R.color.color_progress_full),
            ) as Int
            tvNumPairs.setTextColor(color)
            tvNumPairs.text = "Pairs: ${pairGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (pairGame.haveWonGame()) {
                Snackbar.make(clRoot, "You won!", Snackbar.LENGTH_LONG).show()
            }
        }
        tvNumFlips.text = "Flips: ${pairGame.getNumFlips()}"
        adaptor.notifyDataSetChanged()
    }
}