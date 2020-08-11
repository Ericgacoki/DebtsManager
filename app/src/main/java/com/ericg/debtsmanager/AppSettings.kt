/*
 * Copyright (c)  Updated by eric on  8/7/20 1:07 PM
 */

package com.ericg.debtsmanager

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import com.ericg.debtsmanager.extensions.toast
import kotlinx.android.synthetic.main.activity_app_settings.*

/**
 * @author eric
 * @date 8/7/20
 */
class AppSettings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)

        updateSettings()
    }

    @Suppress("LocalVariableName")
    private fun updateSettings() {
        toast("changes are saved as they happen")

        // exit menu

        val EXIT_BY_EXTERNAL_CLICK = "exitByExternalClick"
        val exitMenuExternally = getSharedPreferences(EXIT_BY_EXTERNAL_CLICK, 0)
        val exitEditor = exitMenuExternally.edit()

        switchExitMenuExternally.apply {
            isChecked = exitMenuExternally.getBoolean(EXIT_BY_EXTERNAL_CLICK, false)

            setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
                exitEditor.putBoolean(EXIT_BY_EXTERNAL_CLICK, switchExitMenuExternally.isChecked).apply()
            }
        }

        // mute notifications

        val MUTE_NOTIFICATION = "muteNotification"
        val muteNotification = getSharedPreferences(MUTE_NOTIFICATION, 0)
        val muteEditor = muteNotification.edit()

        switchMuteNotifications.apply {
            isChecked = muteNotification.getBoolean(MUTE_NOTIFICATION, false)

            setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
                muteEditor.putBoolean(MUTE_NOTIFICATION, switchMuteNotifications.isChecked).apply()
            }
        }

        // todo() implement the remaining settings

        //mute ads

        // dark mode
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}