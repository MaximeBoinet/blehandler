package com.example.utilisateur.kottest

import android.app.Activity
import android.os.Bundle

class MainActivity : Activity(), BleActionHandler {

    //Exemple du fameux BleActionHandler du grand developeur maxime boinet.
    private lateinit var bleHandler: BleHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialisation de notre ble handler, avec notre "action handler"
        // qui doit être une activity implementant BleAction Handler
        bleHandler = BleHandler(this)

        //Pour parler à la device si besoin (envoyer la valeur voulu
        bleHandler.sendData(4)

    }

    //Le blehandler enverra ses valeurs à l'activity via cette fonction.
    override fun handleReceveidValue(actionValue: Short) {
        TODO("associer les valeur recu aux action voulu")
    }
}