package com.example.pairscardgame

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.pairscardgame.models.BoardSize

class ImagePickerAdaptor(
    private val context: Context,
    private val chosenImgUris: List<Uri>,
    private val boardSize: BoardSize
) : RecyclerView.Adapter<ImagePickerAdaptor.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCustomImage = itemView.findViewById<ImageView>(R.id.ivCustomImage)

        fun bind(uri: Uri) {
            ivCustomImage.setImageURI(uri)
            ivCustomImage.setOnClickListener(null)
        }
        fun bind() {
            ivCustomImage.setOnClickListener {
                // launch intent to select photos
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.card_image, parent, false)
//        val cardWidth = parent.width / boardSize.getWidth()
        val cardHeight = parent.height / boardSize.getHeight()
        var cardWidth = cardHeight / 4 * 3
        if (cardWidth * boardSize.getWidth() > parent.width) {
            cardWidth = cardHeight / 8 * 5
        }
        if (cardWidth * boardSize.getWidth() > parent.width) {
            cardWidth = cardHeight / 2
        }
        val layoutParams = view.findViewById<ImageView>(R.id.ivCustomImage).layoutParams
        layoutParams.width = cardWidth
        layoutParams.height = cardHeight

        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position < chosenImgUris.size) {
            holder.bind(chosenImgUris[position])
        } else {
            holder.bind()
        }
    }

    override fun getItemCount() = boardSize.getNumPairs()

}
