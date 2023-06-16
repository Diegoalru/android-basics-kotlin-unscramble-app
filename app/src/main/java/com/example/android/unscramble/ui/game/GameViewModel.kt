package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val TAG = "[TAG_GameViewModel]"
    private var score = 0
    private var currentWordCount = 0
    private var _currentScrambledWord = "test"

    val currentScrambledWord: String
        get() = _currentScrambledWord

    init {
        Log.d(TAG, "GameViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "GameViewModel destroyed!")
    }
}
