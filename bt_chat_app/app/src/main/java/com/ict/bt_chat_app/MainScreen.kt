package com.ict.bt_chat_app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
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
import java.util.UUID

@Composable
fun MainScreen( navController: NavController,
                devicesList:  MutableList<HashMap<String,String>>?,
                pair:  MutableList<HashMap<String, String>>?,
                bluetoothAdapter: BluetoothAdapter?) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { MyBottomAppbar(navController) }
    )  { innerPading ->
        Column(modifier = Modifier.padding(innerPading)) {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            val thirdHeight = screenHeight / 3
            if (devicesList != null) {
                Log.d("Appareil", "${devicesList}")
                AppareilList(
                    devicesList, Modifier
                        .height(thirdHeight)
                        .fillMaxWidth(), "Near",
                    navController,
                    bluetoothAdapter
                )
            }else{
                Text(text = "Pas d'appareil a decouvrir")
            }
            if (pair != null) {
                AppareilList(
                    listeAppareils = pair, modifier = Modifier
                        .height(thirdHeight * 2)
                        .fillMaxWidth(), title = "Saved",
                    navController,
                    bluetoothAdapter
                )
            }else{
                Text(text = "Pas d'appareil decouvert")
            }
        }
    }
}

@Composable
fun AppareilList(listeAppareils: MutableList<HashMap<String, String>>, modifier: Modifier, title:String,navController: NavController, bluetoothAdapter: BluetoothAdapter?) {
    Column {
        Text(text = title)
        LazyColumn(modifier = modifier) {
            items(listeAppareils) { appareil ->
                AppareilItem(appareil, navController, bluetoothAdapter)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun AppareilItem(appareil: HashMap<String, String>, navController: NavController, bluetoothAdapter: BluetoothAdapter?) {
    Column(modifier = Modifier
        .padding(16.dp)
        .clickable {
            val deviceAddress = appareil["address"]
            val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceAddress)
            device?.let {
                if (it.bondState == BluetoothDevice.BOND_NONE) {
                    // Initier l'appairage.
                    it.createBond()
                } else {
                    // L'appareil est déjà appairé, naviguer vers l'écran de chat.
                    startAutomaticNegotiation(BluetoothAdapter.getDefaultAdapter(), it)
                    navController.navigate(Path.Chat.name + "/${appareil["nom"] ?: ""}/${appareil["address"] ?: ""}")
                    Log.d("negociation:", " Reuissite de la negociation");
                }
            }
        }
    ) {
        Text(text = appareil["nom"] ?: "")
        Text(text = appareil["address"] ?: "")
    }
}

@SuppressLint("HardwareIds")
fun startAutomaticNegotiation(myDevice: BluetoothAdapter, otherDevice: BluetoothDevice) {
    val myMac = myDevice.address.replace(":", "")
    val otherMac = otherDevice.address.replace(":", "")

    val myUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    val otherUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    if (myMac < otherMac) {
        // Cet appareil agira en tant que serveur
        val acceptThread = AcceptThread(BluetoothAdapter.getDefaultAdapter(), myUuid)
        acceptThread.start()
    } else {
        // Cet appareil agira en tant que client
        val connectThread = ConnectThread(otherDevice, otherUuid)
        connectThread.start()
    }
}

