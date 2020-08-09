/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager


import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ericg.debtsmanager.fragments.*
import com.ericg.debtsmanager.utils.toast
import kotlinx.android.synthetic.main.activity_parent.*
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class ParentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_parent)

/*
         use the icon color as it is.
*/
        bottomNav.itemIconTintList = null

        // set debtors as the default fragment
        manageFragment(fragment = Debtors())
        disableCurrent(R.id.debtors)
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
                R.id.loans -> {
                    manageFragment(Loans())
                }
                R.id.installments -> {
                    manageFragment(Installments())
                }
            }
            true
        }
    }


    private fun manageFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .attach(fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun disableCurrent(current: Int) {
        val views: List<Int> =
            listOf(R.id.profile, R.id.debtors, R.id.myDebts, R.id.loans, R.id.installments)
        views.forEach { view ->
            findViewById<View>(view).isClickable = view != current
        }
    }

    private var backPressEnabled: Boolean = false

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        Handler().postDelayed({ backPressEnabled = false }, 2000)
        if (backPressEnabled) {
            super.onBackPressed()
            //finish()
        } else {
            toast("press again to exit")
            backPressEnabled = true
        }
    }
}