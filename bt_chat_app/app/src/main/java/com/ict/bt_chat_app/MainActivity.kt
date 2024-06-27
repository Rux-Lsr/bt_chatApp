@file:Suppress("DEPRECATION")

package com.ict.bt_chat_app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ict.bt_chat_app.ui.theme.Bt_chat_appTheme

class MainActivity : ComponentActivity() {
    val REQUEST_ENABLE_BT = 0
    val REQUEST_DISCOVER_BT = 1
    val listeAppareils: MutableList<HashMap<String, String>> = mutableStateListOf()
    private var infoAppareilAssocier: Set<BluetoothDevice>? = null
    var bluetoothAdapter: BluetoothAdapter? = null
    var isScanning = false // Track scanning state

    // Permission Request Codes
    private val PERMISSIONS_REQUEST_CODE = 101

    // Permissions Array
    private val permissionsArray = arrayOf(
        Manifest.permission.BLUETOOTH_ADVERTISE,
        Manifest.permission.BLUETOOTH_PRIVILEGED,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN
    )

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissions()

        // Initialize Bluetooth adapter
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        // Check if Bluetooth is supported
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device.", Toast.LENGTH_LONG).show()
            return
        }

        // Register Broadcast Receivers
        registerReceiver(receiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        registerReceiver(rechercheBluetoothReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(receiverDiscoveryStarted, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(receiveDiscoveryFinished, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))

        // Set up Composable UI
        setContent {
            Bt_chat_appTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Buttons
                        Button(onClick = { activerBluetooth() }) {
                            Text("Enable Bluetooth")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { rechercherAppareil() }) {
                            Text(if (isScanning) "Stop Scanning" else "Scan for Devices")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // List of Discovered Devices
                        AppareilList(listeAppareils = listeAppareils)
                    }
                }
            }
        }
    }

    // Permission Request
    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val missingPermissions = permissionsArray.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (missingPermissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, missingPermissions.toTypedArray(), PERMISSIONS_REQUEST_CODE)
            }
        }
    }

    // Handle Permission Request Result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed with Bluetooth operations
                initListeAppareilAssocie()
            } else {
                // Some permissions were denied, handle accordingly
                Toast.makeText(this, "Bluetooth permissions are required.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Initialize List of Paired Devices
    fun initListeAppareilAssocie() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }
        listeAppareils.clear()
        infoAppareilAssocier = bluetoothAdapter?.bondedDevices
        infoAppareilAssocier?.forEach { device ->
            val element = HashMap<String, String>()
            element["nom"] = device.name
            element["address"] = device.address
            listeAppareils.add(element)
        }
    }

    // Start/Stop Bluetooth Scanning
    @SuppressLint("MissingPermission")
    fun rechercherAppareil() {
        if (isScanning) {
            stopDiscovery()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_SCAN
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
            }
            isScanning = true
            bluetoothAdapter?.startDiscovery()
            // Set a timer to stop scanning after 30 seconds
            val handler = Handler(Looper.getMainLooper())
            val stopRunnable: Runnable = Runnable {
                stopDiscovery()
            }
            handler.postDelayed(stopRunnable, 30000)
        }
    }

    // Stop Bluetooth Discovery
    @SuppressLint("MissingPermission")
    private fun stopDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        isScanning = false
    }

    // Enable Bluetooth
    @SuppressLint("MissingPermission")
    fun activerBluetooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
    }

    // Enable Bluetooth Visibility (Discoverable Mode)
    @SuppressLint("MissingPermission")
    fun activerVisibiliteBluetooth() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        startActivityForResult(discoverableIntent, REQUEST_DISCOVER_BT)
    }

    // Broadcast Receiver for Bluetooth State Changes
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
                when (state) {
                    BluetoothAdapter.STATE_OFF -> Toast.makeText(context, "Bluetooth Off", Toast.LENGTH_SHORT).show()
                    BluetoothAdapter.STATE_ON -> {
                        initListeAppareilAssocie()
                        Toast.makeText(context, "Bluetooth On", Toast.LENGTH_SHORT).show()
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> Toast.makeText(context, "Turning On", Toast.LENGTH_SHORT).show()
                    BluetoothAdapter.STATE_TURNING_OFF -> Toast.makeText(context, "Turning Off", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Broadcast Receiver for Device Discovery
    private val rechercheBluetoothReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val element = HashMap<String, String>()
                element["nom"] = device?.name ?: ""
                element["address"] = device?.address ?: ""
                if (!listeAppareils.any { it["address"] == element["address"] }) {
                    listeAppareils.add(element)
                }
            }
        }
    }

    // Broadcast Receiver for Discovery Start/Finish
    private val receiverDiscoveryStarted: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_DISCOVERY_STARTED) {
                Toast.makeText(context, "Scanning for Devices...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiveDiscoveryFinished: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                Toast.makeText(context, "Scan Complete.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun AppareilList(listeAppareils: MutableList<HashMap<String, String>>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(listeAppareils) { appareil ->
            AppareilItem(appareil)
        }
    }
}

@Composable
fun AppareilItem(appareil: HashMap<String, String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = appareil["nom"] ?: "", style = MaterialTheme.typography.displayLarge)
            Text(text = appareil["address"] ?: "")
        }
    }
}



