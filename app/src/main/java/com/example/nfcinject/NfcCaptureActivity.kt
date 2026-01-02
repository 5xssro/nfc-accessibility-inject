package com.example.nfcinject

import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.Charset

class NfcCaptureActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handle(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
    }

    private fun handle(intent: Intent) {
        val action = intent.action ?: return
        if (action != NfcAdapter.ACTION_NDEF_DISCOVERED &&
            action != NfcAdapter.ACTION_TECH_DISCOVERED &&
            action != NfcAdapter.ACTION_TAG_DISCOVERED
        ) return

        val payload = readNdefText(intent) ?: return

        getSharedPreferences("nfc_inject", MODE_PRIVATE)
            .edit()
            .putString("pending_text", payload)
            .apply()

        sendBroadcast(Intent(NfcInjectAccessibilityService.ACTION_INJECT))

        finish()
    }

    private fun readNdefText(intent: Intent): String? {
        val rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) ?: return null
        val msg = rawMsgs.firstOrNull() as? NdefMessage ?: return null
        val record = msg.records.firstOrNull() ?: return null
        val payload = record.payload ?: return null
        if (payload.isEmpty()) return null

        val status = payload[0].toInt()
        val langLength = status and 0x3F
        val encoding: Charset =
            if ((status and 0x80) == 0) Charsets.UTF_8 else Charset.forName("UTF-16")

        return String(payload, 1 + langLength, payload.size - 1 - langLength, encoding).trim()
    }
}
