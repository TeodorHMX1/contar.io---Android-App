package io.contar.app.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.contar.app.activities.WebsiteViewActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, WebsiteViewActivity::class.java)
        startActivity(intent)
        finish()
    }
}