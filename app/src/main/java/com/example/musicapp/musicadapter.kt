package com.example.musicapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MusicAdapter(private val context: Context, private val playClickListener: PlayClickListener, private val pauseClickListener: PauseClickListener) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var musicList: List<music> = listOf()

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleTextView: TextView = itemView.findViewById(R.id.songname)
        val artistTextView: TextView = itemView.findViewById(R.id.artistname)
        val playButton: ImageButton = itemView.findViewById(R.id.play)
        val pauseButton: ImageButton = itemView.findViewById(R.id.pause)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_music, parent, false)
        return MusicViewHolder(view)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currentMusic = musicList[position]

        // Load image using Glide or any other image loading library
        Glide.with(context)
            .load(currentMusic.imageUrl)
            .into(holder.imageView)

        holder.titleTextView.text = currentMusic.title
        holder.artistTextView.text = currentMusic.artist

        holder.playButton.setOnClickListener {
            playClickListener.onPlayClicked(position)
        }

        holder.pauseButton.setOnClickListener {
            pauseClickListener.onPauseClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun setData(newMusicList: List<music>) {
        musicList = newMusicList
        notifyDataSetChanged()
    }

    fun getItem(position: Int): music {
        return musicList[position]
    }
}


interface PlayClickListener {
    fun onPlayClicked(position: Int)
}

interface PauseClickListener {
    fun onPauseClicked(position: Int)
}