package com.ict.bt_chat_app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectThread(device: BluetoothDevice, uuid: UUID) : Thread() {
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(uuid)
    }

    public override fun run() {
        // Annuler la découverte car elle ralentit la connexion.
        BluetoothAdapter.getDefaultAdapter()?.cancelDiscovery()

        mmSocket?.use { socket ->
            // Connectez-vous à l'appareil Bluetooth distant via le socket.
            socket.connect()
            Log.d("connexion", "Connected as a Client")

            // Gérer la connexion.
            manageConnectedSocket(socket)
        }
    }

    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            // .
            Log.d("erreur : connexion", "UUne erreur s'est produite lors de la fermeture du socket")
        }
    }

    private fun manageConnectedSocket(socket: BluetoothSocket) {
        val inputStream: InputStream?
        val outputStream: OutputStream?

        try {
            inputStream = socket.inputStream
            outputStream = socket.outputStream
        } catch (e: IOException) {
            Log.d("erreur : connexion", "Une erreur s'est produite lors de l'obtention des flux d'entrée/sortie.")
            return
        }

        val buffer = ByteArray(1024)  // tampon pour le flux
        var bytes: Int

        // Continuez à écouter InputStream jusqu'à une exception.
        while (true) {
            try {
                Log.d("connexion", "lecture de inputstream")
                // Lire depuis InputStream.
                bytes = inputStream.read(buffer)
                val incomingMessage = String(buffer, 0, bytes)
                // Gérer le message reçu ici.

                // Envoyer une réponse.
                val responseMessage = "Ceci est une réponse du client."
                outputStream.write(responseMessage.toByteArray())
            } catch (e: IOException) {
               Log.d("erreur : connexion", "Une erreur s'est produite lors de la lecture ou de l'écriture des flux.")
                break
            }
        }
    }
}
