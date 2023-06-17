/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    companion object {
        private const val TAG = "[TAG_GameFragment]"
    }

    private val viewModel: GameViewModel by viewModels()

    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        Log.d(TAG, "GameFragment created/re-created!")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the viewmodel for databinding - this allows the bound layout access
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS

        // Set the lifecycle owner to the lifecycle of the view
        binding.lifecycleOwner = viewLifecycleOwner

        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        updateNextWordOnScreen()

        viewModel.score.observe(viewLifecycleOwner) { newScore ->
            binding.score.text = getString(R.string.score, newScore)
        }
        viewModel.currentWordCount.observe(viewLifecycleOwner) { newWordCount ->
            binding.wordCount.text = getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
        }
        viewModel.currentScrambledWord.observe(viewLifecycleOwner) { newWord ->
            binding.textViewUnscrambledWord.text = newWord
        }

        Log.d(
            TAG,
            "Word: ${viewModel.currentScrambledWord} Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}"
        )
    }

    /**
     * Called when the fragment is no longer in use.
     */
    override fun onDetach() {
        super.onDetach()
        Log.d(tag, "GameFragment destroyed!")
    }

    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    private fun onSkipWord() = if (viewModel.nextWord()) {
        setErrorTextField(false)
        updateNextWordOnScreen()
    } else {
        showFinalScoreDialog()
    }

    private fun updateNextWordOnScreen() {
        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord.value
    }

    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }.setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }.setCancelable(false).show()
    }

    /**
     * Sets and resets the text field error status.
     */
    private fun setErrorTextField(error: Boolean) = if (error) {
        binding.textField.isErrorEnabled = true
        binding.textField.error = getString(R.string.try_again)
    } else {
        binding.textField.isErrorEnabled = false
        binding.textInputEditText.text = null
    }

    /**
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        setErrorTextField(false)
        viewModel.reinitializeData()
        updateNextWordOnScreen()
    }

    /**
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }
}
