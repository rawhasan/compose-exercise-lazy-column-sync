package com.example.lazycolumnsync

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lazycolumnsync.data.WordDataSource

class WordViewModel : ViewModel() {
    private val _words = MutableLiveData(listOf<String>())
    val words: LiveData<List<String>>
        get() = _words

    init {
        _words.value = loadWords()
    }

    private fun loadWords(): List<String> {
        return WordDataSource().loadWords()
    }

    fun onAddWord(word: String) {
        _words.value = _words.value?.plus(word)
        Log.d("WordViewModel", "${_words.value}" )

        val myWords = _words.value
        myWords?.sorted()
    }

    fun isWordExists(searchWord: String): Boolean {
        val searchResult = _words.value?.filter { it.lowercase() == searchWord.lowercase() }
        return !searchResult.isNullOrEmpty()
    }
}