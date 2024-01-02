package com.example.myapplication1

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {

    private val bluetoothAdapter: BluetoothAdapter? by lazy { BluetoothAdapter.getDefaultAdapter() }
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sendDataButton: Button = findViewById(R.id.sendButton)
        sendDataButton.setOnClickListener{
            sendData()
        }
    }

    fun sendData() {
        if (bluetoothAdapter == null) {
            showToast("Bluetooth is not supported on this device")
            return
        }

        if (!bluetoothAdapter!!.isEnabled) {
            showToast("Bluetooth is not enabled")
            return
        }
        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter!!.bondedDevices
        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH)== PackageManager.PERMISSION_GRANTED){
            if (pairedDevices.isEmpty()) {
                showToast("No paired devices found")
                return
            }
            else{
                requestPermissions(arrayOf(android.Manifest.permission.BLUETOOTH), REQUEST_BLUETOOTH_PERMISSION)
            }
        }


        val device: BluetoothDevice = pairedDevices.first()

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.randomUUID())
            bluetoothSocket!!.connect()

            outputStream = bluetoothSocket!!.outputStream
            val message = "Hello Device 2!"
            outputStream!!.write(message.toByteArray())

            showToast("Data sent successfully")

        } catch (e: IOException) {
            showToast("Error sending data: ${e.message}")
        } finally {
            try {
                outputStream?.close()
                bluetoothSocket?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
    companion object{
        private const val REQUEST_BLUETOOTH_PERMISSION= 1
    }
}
