package com.ict.bt_chat_app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainScreen( navController: NavController, devicesList:  MutableList<HashMap<String, String>>?, pair:  MutableList<HashMap<String, String>>?) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomAppbar(navController) }
    )  { innerPading ->
        Column(modifier = Modifier.padding(innerPading)) {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            val thirdHeight = screenHeight / 3
            if (devicesList != null) {
                AppareilList(
                    devicesList, Modifier
                        .height(thirdHeight)
                        .fillMaxWidth(), "Near",
                    navController
                )
            }
            if (pair != null) {
                AppareilList(
                    listeAppareils = pair, modifier = Modifier
                        .height(thirdHeight * 2)
                        .fillMaxWidth(), title = "Saved",
                    navController
                )
            }
        }
    }
}

@Composable
fun AppareilList(listeAppareils: MutableList<HashMap<String, String>>, modifier: Modifier, title:String,navController: NavController) {
    Column {
        Text(text = title)
        LazyColumn(modifier = modifier) {
            items(listeAppareils) { appareil ->
                AppareilItem(appareil, navController)
            }
        }
    }
}

@Composable
fun AppareilItem(appareil: HashMap<String, String>, navController: NavController) {
    Column(modifier = Modifier
        .padding(16.dp)
        .clickable {
            navController.navigate(Path.Chat.name+"/${appareil["nom"] ?: ""}/${appareil["address"] ?: ""}")
        }
    ) {
        Text(text = appareil["nom"] ?: "")
        Text(text = appareil["address"] ?: "")
    }
}

