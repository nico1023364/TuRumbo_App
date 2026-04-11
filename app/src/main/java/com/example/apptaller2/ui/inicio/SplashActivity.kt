package com.example.apptaller2.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.apptaller2.R
import kotlin.jvm.java

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        /*
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, InicioActivity::class.java))
            finish()
        },3000)*/
    }
}