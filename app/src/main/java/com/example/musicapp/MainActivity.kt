package com.example.musicapp

// MainActivity.kt

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.widget.SearchView

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException

class MainActivity : AppCompatActivity(), PlayClickListener, PauseClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MusicAdapter
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recycler)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = MusicAdapter(this, this, this)
        recyclerView.adapter = adapter
        mediaPlayer = MediaPlayer()

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { fetchData(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Fetch initial data
        fetchData("taylorswift") // Default search query
    }

    private fun fetchData(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val musicList = ArrayList<music>()
                val url =
                    "https://deezerdevs-deezer.p.rapidapi.com/search?q=$query&rapidapi-key=496a94201fmsh7320ecd8db3954bp183861jsnd8a699f37d58"
                val request = JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    { response ->
                        try {
                            val dataArray = response.getJSONArray("data")
                            for (i in 0 until dataArray.length()) {
                                val musicObj = dataArray.getJSONObject(i)
                                val title = musicObj.getString("title")
                                val artistObj = musicObj.getJSONObject("artist")
                                val artist = artistObj.getString("name")
                                val previewUrl = musicObj.getString("preview")
                                val albumObj = musicObj.getJSONObject("album")
                                val imageUrl = albumObj.getString("cover_medium")

                                val music = music(title, artist, previewUrl, imageUrl)
                                musicList.add(music)
                            }
                            adapter.setData(musicList)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    { error ->
                        error.printStackTrace()
                    }
                )

                val requestQueue = Volley.newRequestQueue(this@MainActivity)
                requestQueue.add(request)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPlayClicked(position: Int) {
        val music = adapter.getItem(position)

        // Set data source for MediaPlayer
        mediaPlayer.apply {
            reset() // Reset MediaPlayer before setting new data source
            setDataSource(music.previewUrl)
            prepareAsync() // Asynchronous preparation
            setOnPreparedListener {
                it.start() // Start playback when prepared
            }
        }
    }

    override fun onPauseClicked(position: Int) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause() // Pause playback if MediaPlayer is currently playing
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release() // Release MediaPlayer when activity is destroyed
    }
}
