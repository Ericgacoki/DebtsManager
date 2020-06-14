/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */

package com.ericg.debtsmanager


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ericg.debtsmanager.fragments.*
import kotlinx.android.synthetic.main.activity_parent.*

class ParentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_parent)
        bottomNav.itemIconTintList = null

        // set debtors as the default fragment
        manageFragment(Debtors())
        navigateTo()
    }

    private fun navigateTo() {
        bottomNav.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {

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

    private fun manageFragment(display: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, display)
            .attach(display)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
}