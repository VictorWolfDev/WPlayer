package com.jwolf.wplayer

import androidx.lifecycle.ViewModel
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow

class MusicPlayerViewModel: ViewModel() {
    private val playerState = MutableStateFlow<ExoPlayer?>(null)
}