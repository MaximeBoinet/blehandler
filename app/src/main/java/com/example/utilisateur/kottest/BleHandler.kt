package com.example.utilisateur.kottest

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.BluetoothAdapter.STATE_CONNECTED
import android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues.TAG
import android.util.Log
import java.util.*
import kotlin.experimental.and

class BleHandler(private var actionHandler: BleActionHandler) {

    private var service_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
    private var TX_char_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
    private var RX_char_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
    private var UUID_notif = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private lateinit var myDevice: BluetoothDevice
    private val mBluetoothManager: BluetoothManager = (actionHandler as Activity).getSystemService(android.content.Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val mBluetoothAdapter: BluetoothAdapter = mBluetoothManager.adapter
    private lateinit var gatt: BluetoothGatt
    private var mBluetoothLeScanner: BluetoothLeScanner
    private var found: Boolean = false

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            if (result != null) {
                val deviceName = result.device.name
                if (deviceName != null && deviceName == ("MyHologramCommand")) {
                    myDevice = result.device
                    found = true
                    communicate()
                }
            }
        }
    }

    init {
        mBluetoothLeScanner = mBluetoothAdapter.bluetoothLeScanner
        mBluetoothLeScanner.startScan(scanCallback)
    }

    private fun communicate() {
        mBluetoothLeScanner.stopScan(scanCallback);
        gatt = myDevice.connectGatt(actionHandler as MainActivity, false, gattCallback)

        if (gatt.connect())
            Log.i(TAG, "Connection: managed connection")
        else
            Log.e(TAG, "Fail: failed to connect")
    }

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)

            if (newState == STATE_CONNECTED)
                gatt.discoverServices()

            if (newState == STATE_DISCONNECTED)
                mBluetoothLeScanner.startScan(scanCallback)
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            val characteristic = gatt.getService(service_UUID).getCharacteristic(RX_char_UUID)
            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic.getDescriptor(UUID_notif)
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            val characteristic = gatt.getService(service_UUID).getCharacteristic(RX_char_UUID)
            gatt.setCharacteristicNotification(characteristic, true)
            characteristic.value = byteArrayOf(1, 1)
            gatt.writeCharacteristic(characteristic)
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            Log.d("CharChanged", (characteristic.value[0] and 0xFF.toByte()).toString())
            val receivedVal = (characteristic.value[0] and 0xFF.toByte()).toShort()
            actionHandler.handleReceveidValue(receivedVal)
        }
    }

    fun sendData(value: Short) {
        val service = gatt.getService(service_UUID)

        if (service != null) {
            val characteristic = service.getCharacteristic(TX_char_UUID)

            if (characteristic != null) {
                characteristic.value = byteArrayOf(value.toByte())
                gatt.writeCharacteristic(characteristic)
            }
        }
    }
}