package com.example.lazycolumnsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Exercise project to sync list on a LazyColumn after adding and deleting items

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LazyColumnSyncApp()
        }
    }
}

@Composable
fun LazyColumnSyncApp() {
    var newWord by remember { mutableStateOf("") }
    Column {
        TopAppBar(title = { Text(stringResource(id = R.string.app_name)) })

        Column(Modifier.padding(16.dp)) {

            // TextField and the Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextField(
                    value = newWord,
                    onValueChange = { newWord = it },
                    label = { Text("New Word") },
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
                Button(
                    modifier = Modifier
                        .height(56.dp)
                        .padding(start = 8.dp)
                        .fillMaxWidth(),
                    onClick = { /*TODO*/ }) {
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
                modifier = Modifier.weight(1f), // fill up the remaining space
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(50) {
                    WordItemLayout(it)
                }
            }

            Spacer(modifier = Modifier.padding(top = 16.dp))

            // Scroll Buttons on the bottom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text("Scroll to Top")
                }

                Button(onClick = { /*TODO*/ }, Modifier.padding(start = 8.dp)) {
                    Text("Scroll to Bottom")
                }
            }
        }
    }
}

@Composable
fun WordItemLayout(index: Int) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.primaryVariant)
            .padding(vertical = 16.dp, horizontal = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.ic_star), contentDescription = null)
        Text(
            text = "Word # $index",
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f),
            color = Color.White,
            fontSize = 20.sp
        )
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp, bottom = 12.dp)
                .size(16.dp),
            tint = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LazyColumnSyncApp()
}