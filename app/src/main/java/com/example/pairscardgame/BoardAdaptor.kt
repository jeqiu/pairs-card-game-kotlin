package com.example.pairscardgame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.pairscardgame.models.BoardSize
import com.example.pairscardgame.models.PairCard

class BoardAdaptor(
    private val context: Context,
    private val boardSize: BoardSize,
    private val cards: List<PairCard>,
    private val cardClickListener: CardClickListener
) :
    RecyclerView.Adapter<BoardAdaptor.ViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "BoardAdaptor"
    }

    interface CardClickListener {
        fun onCardClick(position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)

        fun bind(position: Int) {
            val gameCard = cards[position]
            imageButton.setImageResource(if (gameCard.isFaceUp) gameCard.identifier else R.drawable.ic_card)

            imageButton.alpha = if (gameCard.isMatched) .4f else 1.0f
//            val colorStateList = if (gameCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
//            ViewCompat.setBackgroundTintList(imageButton, colorStateList)

            imageButton.setOnClickListener {
                Log.i(TAG, "Clicked on position $position")
                cardClickListener.onCardClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardHeight = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)
        var cardWidth = cardHeight / 4 * 3
//        val cardWidth = parent.width / 2 - (2 * MARGIN_SIZE)
//        val cardSideLength = min(cardWidth, cardHeight)

        if (cardWidth * boardSize.getWidth() > parent.width) {
            cardWidth = cardHeight / 8 * 5
        }
        if (cardWidth * boardSize.getWidth() > parent.width) {
            cardWidth = cardHeight / 2
        }
        val view : View = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams : ViewGroup.MarginLayoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardWidth
        layoutParams.height = cardHeight
//        layoutParams.width = cardSideLength
//        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

// shorter syntax for return value: override fun getItemCount() = boardSize.numCards
    override fun getItemCount(): Int {
        return boardSize.numCards
    }

}
