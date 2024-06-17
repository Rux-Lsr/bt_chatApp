package com.ict.bt_chat_app

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument


@Composable
fun Navigation(navController:NavHostController, deviceList: MutableList<HashMap<String, String>>?, pair: MutableList<HashMap<String, String>>?){
    NavHost(
        navController = navController,
        startDestination = Path.Start.name
    ) {
        composable(
            route = Path.Start.name,
        ){
            MainScreen( navController = navController, deviceList, pair)
        }

        composable(route = Path.Chat.name+"/{deviceName}/{deviceMacAddress}",
        arguments = listOf(
            navArgument("deviceName") {
                type = NavType.StringType
            },
            navArgument("deviceMacAddress"){
                type = NavType.StringType
            }
        )
        ){backStackEntry ->
            val deviceName =  backStackEntry.arguments?.getString("deviceName")
            val deviceMac =  backStackEntry.arguments?.getString("deviceMacAddress")

            val msg = listOf(msg("je mange pas de ce pain la", true), msg("je vais te lecher partout", false))
            ChatScreen(messages = msg, navController, listOf(deviceName, deviceMac))
        }

        composable(route = Path.Setting.name){
           EditProfile(navController = navController)
        }
        
    }
}