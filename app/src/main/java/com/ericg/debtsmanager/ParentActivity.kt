/*
 * Copyright (c)  Updated by eric on  9/3/20 11:48 AM
 */

package com.ericg.debtsmanager


import android.content.Intent
import android.graphics.Color.WHITE
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.fragments.Debtors
import com.ericg.debtsmanager.fragments.MyDebts
import com.ericg.debtsmanager.fragments.Profile
import kotlinx.android.synthetic.main.activity_parent.*

class ParentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent)
/*
       use the icon color as it is.
 */     bottomNav.itemIconTintList = null
        // set debtors as the default fragment
        manageFragment(fragment = Debtors())
        disableCurrent(R.id.debtors)
        bottomNav.selectedItemId = R.id.debtors
        navigateTo()
    }

    private fun navigateTo() {
        bottomNav.setOnNavigationItemSelectedListener {
            disableCurrent(it.itemId)
            when (it.itemId) {
                R.id.profile -> {
                    manageFragment(Profile())
                }
                R.id.debtors -> {
                    manageFragment(Debtors())
                }
                R.id.myDebts -> {
                    manageFragment(MyDebts())
                }
                /*  R.id.loans -> {
                      manageFragment(Loans())
                  }
                  R.id.installments -> {
                      manageFragment(Installments())
                  }*/
            }
            true
        }
    }

    private fun manageFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            // .attach(fragment)
            // .addToBackStack("$fragment")
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun disableCurrent(current: Int) {
        val views: List<Int> =
            listOf(R.id.profile, R.id.debtors, R.id.myDebts /* R.id.loans, R.id.installments*/)
        views.forEach { view ->
            findViewById<View>(view).isClickable= view != current
            findViewById<View>(view).isEnabled =  view != current
        }
    }
/*
    override fun onResume() {
        super.onResume()
        manageFragment(Profile())
        disableCurrent(R.id.profile)
        bottomNav.selectedItemId = R.id.profile
        bottomNav.itemRippleColor = getColorStateList(R.color.colorWhite)
    }*/

    private var backPressEnabled: Boolean = false

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        Handler().postDelayed({ backPressEnabled = false }, 2000)
        if (backPressEnabled) {
             // super.onBackPressed()
            startActivity(Intent(this, ExitScreen::class.java))
             finish()
        } else {
            toast("press again to exit")
            backPressEnabled = true
        }
    }
}