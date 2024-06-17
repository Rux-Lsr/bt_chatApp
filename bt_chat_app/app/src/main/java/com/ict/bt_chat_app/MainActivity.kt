@file:Suppress("DEPRECATION")

package com.ict.bt_chat_app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.ict.bt_chat_app.ui.theme.Bt_chat_appTheme

const val REQUEST_FINE_LOCATION = 1
 const val REQUEST_ENABLE_BT = 2

class MainActivity : ComponentActivity() {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val devicesList = mutableStateListOf<HashMap<String, String>>()
    @SuppressLint("MissingPermission")
    private val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    private val pair = mutableStateListOf<HashMap<String, String>>()

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    // Vérifiez si l'appareil n'est pas déjà appairé
                    if (pairedDevices?.contains(device) != true) {
                            val deviceInfo = HashMap<String, String>()
                            deviceInfo["nom"] = device.name ?: "Inconnu"
                            deviceInfo["address"] = device.address
                            devicesList.add(deviceInfo)
                    }
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_FINE_LOCATION)
        }

        pairedDevices?.forEach{ device ->
            val deviceInfo = HashMap<String, String>()
            deviceInfo["nom"] = device.name ?: "Inconnu"
            deviceInfo["address"] = device.address
            pair.add(deviceInfo);
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        //bluetoothAdapter?.startDiscovery()

        setContent {
            val navController  = rememberNavController()
            Bt_chat_appTheme {
                Navigation(navController, deviceList = devicesList, pair = pair)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}

