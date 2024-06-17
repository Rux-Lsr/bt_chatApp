package com.ict.bt_chat_app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfile(navController: NavController)
{
    var name by rememberSaveable { mutableStateOf("Negoue Tchinda Patrick") }
        var username by rememberSaveable { mutableStateOf("NTPBreak")  }
    var email by rememberSaveable { mutableStateOf("patricknegoue197@gmail.com")  }
    Scaffold (
        topBar = {
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
                Text(text = "Edit Profile", Modifier.padding(start = 20.dp))
            }
        })
    })
    { it ->
        Column (modifier = Modifier
            .padding(it)
            .padding(45.dp), horizontalAlignment = Alignment.CenterHorizontally){
            Column {

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(shape = RoundedCornerShape(80.dp))
                        .background(color = Color.Gray)
                        .align(Alignment.CenterHorizontally)
                )
                {
                    Text(
                        text = "P",
                        style = TextStyle(
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold
                            ,color = Color.White
                        )
                        , modifier = Modifier.align(Alignment.Center)

                    )
                }

                Spacer(Modifier.padding(bottom = 50.dp))
                Column {
                    Column {
                        TextField(value = name, label = { Text(text = "Full Name") }, onValueChange = {name = it} )
                    }
                    Spacer(Modifier.padding(bottom = 50.dp))

                    Column {
                        TextField(value = username, label = { Text(text = "User name") }, onValueChange = {username = it} )
                    }
                    Spacer(Modifier.padding(bottom = 50.dp))

                    Column {
                        TextField(value = email, label = { Text(text = "Email") }, onValueChange = {email = it} )
                    }

                }
            }
            TextButton(modifier = Modifier.padding(top = 50.dp), onClick = { /*TODO*/ }) {
                Text(text = "Done")
                Icon(imageVector = Icons.Filled.Check, contentDescription ="Done" )
            }

        }

    }

}

@Preview
@Composable
fun PreviewEditProfile()
{
    EditProfile(navController = rememberNavController());
}