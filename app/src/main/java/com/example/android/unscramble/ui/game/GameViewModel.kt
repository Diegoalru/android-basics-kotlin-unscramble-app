package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val TAG = "[TAG_GameViewModel]"

    private val wordsList: MutableList<String> = mutableListOf()

    private var _score = 0
    private var _currentWordCount = 0
    private lateinit var _currentScrambledWord: String
    private lateinit var currentWord: String

    val score: Int
        get() = _score

    val currentWordCount: Int
        get() = _currentWordCount

    val currentScrambledWord: String
        get() = _currentScrambledWord

    init {
        Log.d(TAG, "GameViewModel created!")
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "GameViewModel destroyed!")
    }

    /**
     * Updates currentWord and currentScrambledWord with the next word.
     * Increases the word count.
     */
    private fun getNextWord() {
        currentWord = allWordsList.random() // Select a word from the list
        val tempWord = currentWord.toCharArray() // Convert to char array
        tempWord.shuffle() // Randomize the letters

        // Ensures we don't get the same word
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle() // Randomize again if they're equal
        }

        // If the word is already in the list, get another one
        if (wordsList.contains(currentWord)) {
            getNextWord() // Recursively call until we get a new word
        } else {
            _currentScrambledWord = String(tempWord) // Set the scrambled word
            ++_currentWordCount // Increment the word count
            wordsList.add(currentWord) // Add the word to the list
        }
    }

    /**
     * Returns true if the current word count is less than MAX_NO_OF_WORDS.
     * Updates the next word.
     */
    fun nextWord(): Boolean {
        return if (currentWordCount < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    /**
     * Increases the score by SCORE_INCREASE
     */
    private fun increaseScore() {
        _score += SCORE_INCREASE
    }

    /**
     * Returns true if the player word is correct.
     * Increases the score accordingly.
     */
    fun isUserWordCorrect(word: String): Boolean {
        return if (word.equals(currentWord, false)) {
            increaseScore()
            true
        } else {
            false
        }
    }
}
