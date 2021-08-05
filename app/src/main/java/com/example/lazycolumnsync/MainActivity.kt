package com.example.lazycolumnsync

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

// Exercise project to sync list on a LazyColumn after adding and deleting items

class MainActivity : ComponentActivity() {

    // private val wordViewModel by viewModels<WordViewModel>()

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            //LazyColumnSyncApp(wordViewModel)
            LazyColumnSyncApp()
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun LazyColumnSyncApp(wordViewModel: WordViewModel = viewModel()) {
    val words: List<String> by wordViewModel.words.observeAsState(listOf())
    val wordItemCount = words.size

    var sortAscending by remember { mutableStateOf(true) }
    var newWord by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // experimental compose feature
    val keyboardController = LocalSoftwareKeyboardController.current

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column {
        TopAppBar(
            title = { Text(stringResource(id = R.string.app_name)) },
            actions = {
                IconButton(onClick = {
                    sortAscending = !sortAscending
                    wordViewModel.onSortWord(sortAscending)
                }) {
                    Icon(
                        imageVector = if (sortAscending) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
            })

        Column(Modifier.padding(16.dp)) {

            // TextField and the Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                // Enter new word TextField
                TextField(
                    value = newWord,
                    onValueChange = {
                        // allow only letters as input
                        if (it.all { enteredChar -> enteredChar.isLetter() }) {
                            newWord = it
                        }
                    },
                    label = { Text(if (isError) errorMessage else "New Word") },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }),
                    isError = isError,
                    leadingIcon = {
                        Icon(
                            painterResource(R.drawable.ic_star),
                            contentDescription = null,
                            tint = MaterialTheme.colors.primaryVariant,
                            modifier = Modifier.padding(start = 24.dp, end = 12.dp)
                        )
                    }
                )

                // The Add Button
                Button(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(start = 8.dp)
                        .fillMaxWidth(),
                    onClick = {
                        // TODO: Show trailing icon if error

                        // add the word if valid and is not already in the list
                        // show error message if otherwise, and return
                        if (newWord.trim().isEmpty()) {
                            errorMessage = "Enter a word to add"
                            isError = true
                            return@Button
                        }

                        if (onAddWord(newWord, wordViewModel, keyboardController)) {
                            newWord = ""
                            errorMessage = ""
                            isError = false

                            Toast.makeText(context, "Word added!", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("MainUI", "Word exists. Showing error message.")
                            isError = true
                            errorMessage = "Word already exists!"
                            return@Button
                        }
                    }) {
                    Icon(
                        Icons.Filled.AddCircle,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text("Add", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            LazyColumn(
                state = scrollState,
                modifier = Modifier.weight(1f), // fill up the remaining space
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(words.size) {
                    WordItemLayout(it, words, wordViewModel)
                }
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            // Scroll Buttons on the bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    coroutineScope.launch { scrollState.animateScrollToItem(0) }
                }) {
                    Text("Scroll to Top")
                }

                Button(
                    onClick = {
                        coroutineScope.launch { scrollState.animateScrollToItem(wordItemCount - 1) }
                    },
                    Modifier.padding(start = 8.dp)
                ) {
                    Text("Scroll to Bottom")
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
private fun onAddWord(
    newWord: String,
    wordViewModel: WordViewModel,
    keyboardController: SoftwareKeyboardController?
): Boolean {
    val trimmedWord = newWord.trim()
    var success = false

    if (trimmedWord.isNotEmpty()) {
        success = if (!wordViewModel.isWordExists(trimmedWord)) {
            wordViewModel.onAddWord(trimmedWord)
            keyboardController?.hide()
            true
        } else {
            false
        }
    }

    return success
}

@Composable
fun WordItemLayout(index: Int, words: List<String>, wordViewModel: WordViewModel) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.primaryVariant)
            .padding(vertical = 16.dp, horizontal = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.ic_star), contentDescription = null)
        Text(
            text = words[index].lowercase()
                .replaceFirstChar { it.uppercase() }, // capitalize first letter
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            color = Color.White,
            fontSize = 20.sp
        )
        IconButton(onClick = {
            Log.d("WordItemLayout", words[index])
            wordViewModel.onDeleteWord(words[index])
        }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 12.dp)
                    .size(16.dp),
                tint = Color.White,
            )
        }

    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LazyColumnSyncApp()
}