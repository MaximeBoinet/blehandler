package com.example.utilisateur.kottest

import android.app.Activity
import android.os.Bundle
import android.util.Log

class MainActivity : Activity(), BleActionHandler {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bleH = BleHandler(this)
    }

    override fun handleReceveidValue(actionValue: Short) {
        Log.i("LETAG", "SUKA BLYAT $actionValue")
    }

    companion object {
        val INDEX_DEVICE = "indexDevice"
        private val REQUEST_ENABLE_BT = 5
    }
}