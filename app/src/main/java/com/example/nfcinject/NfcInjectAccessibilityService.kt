package com.example.nfcinject

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class NfcInjectAccessibilityService : AccessibilityService() {

    companion object {
        const val ACTION_INJECT = "com.example.nfcinject.ACTION_INJECT"
    }

    private val rx = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_INJECT) {
                injectPendingTextWithEnter()
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver(rx, IntentFilter(ACTION_INJECT))
    }

    override fun onDestroy() {
        try { unregisterReceiver(rx) } catch (_: Exception) {}
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Injection is triggered explicitly via broadcast.
    }

    override fun onInterrupt() {
        // no-op
    }

    private fun injectPendingTextWithEnter() {
        val prefs = getSharedPreferences("nfc_inject", MODE_PRIVATE)
        val text = prefs.getString("pending_text", null) ?: return

        val root = rootInActiveWindow ?: return

        val focused = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT)
        val target = focused ?: findFirstEditable(root) ?: return

        val args = Bundle().apply {
            putCharSequence(
                AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                text + "\n"
            )
        }

        val ok = target.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        if (!ok) {
            target.performAction(AccessibilityNodeInfo.ACTION_FOCUS)
            target.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        }

        prefs.edit().remove("pending_text").apply()
    }

    private fun findFirstEditable(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isEditable && node.isVisibleToUser) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val r = findFirstEditable(child)
            if (r != null) return r
        }
        return null
    }
}
