package com.example.ayu_do

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class BaseApp : Application(){

    override fun onCreate() {
        super.onCreate()

        setUpAnalitycs()
    }

    private fun setUpAnalitycs(){
        //Analytics Event
        val analytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("messeage","Integracion de Firebase completa")
        analytics.logEvent("InitScreen", bundle)
    }
}