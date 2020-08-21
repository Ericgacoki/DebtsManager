/*
 * Copyright (c)  Updated by eric on  8/7/20 1:07 PM
 */

package com.ericg.debtsmanager

import android.os.Bundle
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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

        val exitMenuExternally = getSharedPreferences(EXIT_BY_EXTERNAL_CLICK, 0)
        val exitEditor = exitMenuExternally.edit()

        switchExitMenuExternally.apply {
            isChecked = exitMenuExternally.getBoolean(EXIT_BY_EXTERNAL_CLICK, false)

            setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
                exitEditor.putBoolean(EXIT_BY_EXTERNAL_CLICK, switchExitMenuExternally.isChecked)
                    .apply()
            }
        }

        // mute notifications

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
        val darkMode = getSharedPreferences(DARK_MODE_ENABLED, 0)
        val modeEditor = darkMode.edit()

        switchEnableDarkMode.apply {
            isChecked = darkMode.getBoolean(DARK_MODE_ENABLED, false)

            setOnCheckedChangeListener {_,darkModeEnabled: Boolean ->
                modeEditor.putBoolean(DARK_MODE_ENABLED, darkModeEnabled).apply()
                if (darkModeEnabled){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()
                } else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    delegate.applyDayNight()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}