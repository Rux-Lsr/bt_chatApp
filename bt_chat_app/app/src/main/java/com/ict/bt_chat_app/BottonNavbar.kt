package com.ict.bt_chat_app

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun MyBottomAppbar(navController: NavController){
    val screenHeight = LocalConfiguration.current.screenHeightDp

    BottomAppBar(modifier = Modifier.fillMaxWidth().height(55.dp)) {
        Row(horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth().height(55.dp) ) {
            BottomMenuItem(
                icon = Icons.Default.Email,
                title = "Chat",
                onClickevent = {
                    navController.navigate(Path.Start.name)
                })
            BottomMenuItem(icon = Icons.Default.AccountCircle, title = "Account", onClickevent = {
                if (navController.currentDestination?.route == Path.Setting.name)  Log.d("Current Route: ","Account") else navController.navigate(
                    Path.Setting.name)
            })
        }
    }
}
@Composable

fun BottomMenuItem(
    icon: ImageVector,
    title: String,
    onClickevent : () -> Unit
) {

    IconButton(
        onClick = {
            onClickevent()
        },
        modifier = Modifier
            .width(100.dp)
            .height(50.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = title, overflow = TextOverflow.Visible,modifier = Modifier)
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewMyBottomAppBar(){
    MyBottomAppbar(navController = rememberNavController())
}