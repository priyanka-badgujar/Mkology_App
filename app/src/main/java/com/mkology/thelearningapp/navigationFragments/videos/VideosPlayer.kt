package com.mkology.thelearningapp.navigationFragments.videos

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.mkology.thelearningapp.OnBackPressedListener
import com.mkology.thelearningapp.R
import com.mkology.thelearningapp.helper.ApplicationConstants


class VideosPlayer : AppCompatActivity(), Player.EventListener, OnBackPressedListener {

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private lateinit var exoplayerView: PlayerView
    private lateinit var videosPlayerHelper: VideosPlayerHelper
    private lateinit var progressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private var readyState = false
    private var videoUrl: String = ApplicationConstants.EMPTY
    private var chapterId: String = ApplicationConstants.EMPTY
    private var chapterName: String = ApplicationConstants.EMPTY
    private var purchaseId: Int = 0
    private var watchCount: Int = 0
    private var fullscreen: Boolean = false

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(this, "exoplayer-sample")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.video_player)
        exoplayerView = findViewById(R.id.exo_player)
        /// nextButton = findViewById(R.id.exo_next)
        //nextButton.visibility = View.GONE
        progressBar = findViewById(R.id.video_progress_bar)
        videosPlayerHelper = VideosPlayerHelper(applicationContext)
        val intent: Intent = intent
        videoUrl = intent.getStringExtra("VideoUrl")
        chapterId = intent.getStringExtra("chapterId")
        chapterName = intent.getStringExtra("chapterName")
        purchaseId = intent.getIntExtra("purchaseId", 0)
        watchCount = intent.getIntExtra("watchCount", 0)
        readyState = false

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = chapterName

        val fullScreenButton = findViewById<ImageView>(R.id.exo_fullscreen_icon)
        fullScreen(fullScreenButton)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()
        val url = videoUrl
        preparePlayer(url, url)

        exoplayerView.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
    }

    private fun buildMediaSource(uri: Uri, type: String): MediaSource {
        return if (type == "dash") {
            DashMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri)
        } else {
            ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri)
        }
    }

    private fun preparePlayer(videoUrl: String, type: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri, type)
        simpleExoplayer.prepare(mediaSource)
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            progressBar.visibility = View.INVISIBLE

        if (!readyState && playbackState == Player.STATE_READY) {
            readyState = true
            videosPlayerHelper.reduceWatchCount(chapterId, purchaseId, watchCount)
        }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        finish()
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        finish()
        return true
    }

    fun fullScreen(fullscreenButton: ImageView) {
        fullscreen = false
        fullscreenButton.setOnClickListener(
                View.OnClickListener {
                    if (fullscreen) {
                        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_fullscreen_black_24dp))
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                        if (supportActionBar != null) {
                            supportActionBar!!.show()
                        }
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        var params: RelativeLayout.LayoutParams =
                                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT)
                        exoplayerView.setLayoutParams(params)
                        fullscreen = false
                    } else {
                        fullscreenButton.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_fullscreen_exit_black_24dp))
                        window.decorView.setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_FULLSCREEN or
                                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                        if (supportActionBar != null) {
                            supportActionBar!!.hide()
                        }
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        var params: RelativeLayout.LayoutParams =
                                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT)
                        exoplayerView.layoutParams = params
                        fullscreen = true
                    }
                }
        )
    }


}