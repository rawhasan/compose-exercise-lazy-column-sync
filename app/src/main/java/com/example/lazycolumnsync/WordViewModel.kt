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

    private var _sortedAscendingOrder = true

    init {
        _words.value = loadWords().sorted()
    }

    // load the words on the list from datasource
    private fun loadWords(): List<String> {
        return WordDataSource().loadWords()
    }

    // add a new word to the list, passed from the UI
    fun onAddWord(word: String) {
        _words.value = _words.value?.plus(word)

        if (_sortedAscendingOrder) {
            _words.value = _words.value?.sorted()
            Log.d("WordViewModel", "Sorting in ascending order")
        } else {
            _words.value = _words.value?.sortedDescending()
            Log.d("WordViewModel", "Sorting in descending order")
        }
    }

    // check if the word passed from the UI is already on the list
    fun isWordExists(searchWord: String): Boolean {
        val searchResult = _words.value?.filter { it.lowercase() == searchWord.lowercase() }
        return !searchResult.isNullOrEmpty()
    }

    // sort the list in ascending or descending order
    // based on the arrow status passing from the UI
    fun onSortWord(ascendingOrder: Boolean) {
        if (ascendingOrder) {
            _words.value = _words.value?.sorted()
            _sortedAscendingOrder = true
        } else {
            _words.value = _words.value?.sortedDescending()
            _sortedAscendingOrder = false
        }
    }

    // delete a word from the list if exists
    fun onDeleteWord(word: String) {
        val index = _words.value?.indexOf(word)

        if (index != null && index >= 0) {
            _words.value = _words.value?.minus(word)
        }
    }
}