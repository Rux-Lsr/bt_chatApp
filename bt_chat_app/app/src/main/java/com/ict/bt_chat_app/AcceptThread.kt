package com.ict.bt_chat_app

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@SuppressLint("MissingPermission")
class AcceptThread(private val bluetoothAdapter: BluetoothAdapter, uuid: UUID) : Thread() {
    private val serverSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        bluetoothAdapter.listenUsingRfcommWithServiceRecord("MonServiceBluetooth", uuid)
    }

    override fun run() {
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                serverSocket?.accept()

            } catch (e: IOException) {
                shouldLoop = false
                null
            }
            Log.d("connexion", "Connected as a Client")
            socket?.also {
                // Gérer la connexion dans un thread séparé.
                manageConnectedSocket(it)
                serverSocket?.close()
                shouldLoop = false
            }
            Log.d("connexion", "Deconnexion du serveur")
        }
    }

    fun cancel() {
        try {
            serverSocket?.close()
        } catch (e: IOException) {
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
                Log.d("", "Une erreur s'est produite lors de la lecture ou de l'écriture des flux.")

                // Envoyer une réponse.
                val responseMessage = "Ceci est une réponse."
                outputStream.write(responseMessage.toByteArray())
            } catch (e: IOException) {
                Log.d("erreur : connexion", "Une erreur s'est produite lors de la lecture ou de l'écriture des flux.")
                break
            }
        }
    }
}

