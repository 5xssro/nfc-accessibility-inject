package com.example.nfcinject

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        root.addView(TextView(this).apply {
            text = "Použitie (Enterprise Browser / Chrome):\n" +
                   "1) Zapni Accessibility službu: Nastavenia → Prístupnosť → Nainštalované služby → NFC Inject → Zapnúť\n" +
                   "2) Otvor web stránku a klikni do textového poľa (musí byť focus).\n" +
                   "3) Klikni nižšie na 'Spustiť NFC čítanie' a prilož NFC tag (NDEF text).\n" +
                   "4) Text sa vloží do focused poľa a pridá sa ENTER."
        })

        root.addView(Button(this).apply {
            text = "Otvoriť nastavenia prístupnosti"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        })

        root.addView(Button(this).apply {
            text = "Spustiť NFC čítanie"
            setOnClickListener {
                startActivity(Intent(this@MainActivity, NfcCaptureActivity::class.java))
            }
        })

        setContentView(root)
    }
}
