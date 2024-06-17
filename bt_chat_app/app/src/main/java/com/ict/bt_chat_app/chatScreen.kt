package com.ict.bt_chat_app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(messages: List<msg>, navController: NavController, deviceInfo: List<String?>) {
   Scaffold(
       topBar = {
           AppBarTop(navController, deviceInfo[0])
       },
       bottomBar = {
           UserInputField(onMessageSent = {

           })
       }) { innerPadding ->
       Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier
           .fillMaxSize()
           .padding(innerPadding)) {
           LazyColumn {
               items(messages) { message ->
                   ChatItem(message.text, message.author)
               }
           }
       }
   }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppBarTop(navController: NavController, title:String? = "Not defined") {
    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = { navController.popBackStack() },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "quit"
                )
            }
            if (title != null) {
                Text(text = title, Modifier.padding(start = 20.dp))
            }
        }
    })
}

@Composable
fun ChatItem(message: String, author: Boolean) {
    val arrangement = if(author == true)
        Arrangement.End
    else
        Arrangement.Start;
    Card(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row (horizontalArrangement = arrangement){
            Text(
                text = message,
                style = MaterialTheme.typography.body2,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun UserInputField(onMessageSent: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    // Définissez ici le style et la mise en page du champ de saisie utilisateur
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Tapez quelque chose...") }
        )
        IconButton(onClick = { onMessageSent(text); text = "" }) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Envoyer")
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PrevChatScreen(){
    val messages = listOf(msg("Bonjour", false), msg("Bonsoir", true))
    ChatScreen(messages, rememberNavController(), listOf("Rux", "Lsr"))
}