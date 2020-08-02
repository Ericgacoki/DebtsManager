/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager


import android.os.Bundle
import android.os.Handler
import android.widget.AbsListView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ericg.debtsmanager.fragments.*
import kotlinx.android.synthetic.main.activity_parent.*

class ParentActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_parent)

/*
         use the icon color as it is.
*/
        bottomNav.itemIconTintList = null

        // set debtors as the default fragment
        manageFragment(fragment = Debtors())
        navigateTo()
    }

    private fun navigateTo() {
        bottomNav.setOnNavigationItemSelectedListener {

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
            //.addToBackStack(fragment.toString())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private var backPressEnabled: Boolean = false

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        Handler().postDelayed({ backPressEnabled = false }, 2000)
        if (backPressEnabled) {
            super.onBackPressed()
            //finish()
        } else {
            Toast.makeText(this, "press again to exit", LENGTH_SHORT).show()
            backPressEnabled = true
        }
    }
}