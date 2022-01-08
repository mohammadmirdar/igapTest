package net.igap

import android.app.Application
import net.igap.network_module.LookUpClass
import net.igap.network_module.WebSocketClient

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LookUpClass.fillArrays()
        WebSocketClient.getInstance()
    }
}