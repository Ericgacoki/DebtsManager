/*
 * Copyright (c)  Updated by eric on  6/14/20 2:13 PM
 */


@file:Suppress("DEPRECATION")

package com.ericg.debtsmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import com.ericg.debtsmanager.*
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import com.ericg.debtsmanager.adapters.InstallmentsAdapter
import com.ericg.debtsmanager.adapters.LoansAdapter
import com.ericg.debtsmanager.adapters.MyDebtsAdapter
import com.ericg.debtsmanager.admin.AboutDeveloper
import com.ericg.debtsmanager.auth.CreateAccountActivity
import com.ericg.debtsmanager.auth.SignInActivity
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.extensions.userSharedPrefs
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_delete_or_log_out.view.*
import kotlinx.android.synthetic.main.dialog_rate_app.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

class Profile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onStart() {
        super.onStart()
        // TODO load image if its not set otherwise leave a place holder
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateProfile()
        handleClicks()
        handleCustomMenuClicks()

    }

    @Suppress("LocalVariableName")
    private fun updateProfile() {

        if (mUser != null) {
            val mUserEmail: String? = mUser!!.email
            if (mUserEmail != null) {
                profileUserEmail!!.text = mUserEmail.toString().trim()
            }
        }

        val USER_NAME = "userName"
        val userName = this.activity!!.getSharedPreferences(USER_NAME, 0)
            .getString(USER_NAME, "Not registered")
        profileUserName.text = userName!!.trim()

        // todo use coroutine to fetch data from firestore

        pDebtors.text = Debtors().debtorsList.size.toString()
        pMyDebts.text = "10"
        pLoans.text = LoansAdapter().itemCount.toString()
        pInstallments.text = InstallmentsAdapter().itemCount.toString()

    }

    @Suppress("LocalVariableName")
    private fun customMenu(state: String) {
        if (state.toLowerCase(Locale.ROOT).trim() == "show") {
            layMenuParent.apply {
                visibility = VISIBLE
                setOnClickListener {

                    // check from settings if user has disabled this feature. not all users might like to exit the menu by clicking outside it.

                    val EXIT_BY_EXTERNAL_CLICK = "exitByExternalClick"
                    if (activity!!.getSharedPreferences(EXIT_BY_EXTERNAL_CLICK, 0)
                            .getBoolean(EXIT_BY_EXTERNAL_CLICK, false)
                    ) {
                        customMenu("exit")
                    }
                }
            }
            return
        }

        if (state.toLowerCase(Locale.ROOT).trim() == "exit") {
            // just making sure that layMenuParent is visible otherwise views would be null
            layMenuParent.apply {
                visibility = VISIBLE
            }

            val viewsToAnimate: Array<Button> =
                arrayOf(pBtnEditAccount, pBtnAboutDev, pBtnRate5Star) +
                        arrayOf(pBtnAppSettings, pBtnShareToOthers)

            viewsToAnimate.forEach { view ->
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_all_to_right))
            }
            layMenuParent.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
            Handler().postDelayed({
                layMenuParent.visibility = INVISIBLE
            }, 400L)

            return
        }
    }

    @Suppress("DEPRECATION")
    private fun handleClicks() {
        deleteAccount.setOnClickListener {
            deleteAccount.startAnimation(
                AnimationUtils.loadAnimation(
                    context, R.anim.shake_up
                )
            )
            Handler().postDelayed({ kotlin.run { deleteOrLogOut() } }, 90L)
        }

        showMenu.setOnClickListener {
            customMenu("show")
        }

        exitMenu.setOnClickListener {
            customMenu("exit")
        }

        layMenuParent!!.setOnClickListener {
            // this prevents clicks to all views with lower elevation ie views below it
        }

        var zoom = false
        profileDp.setOnClickListener {
            // zoom dp to full size image
            // todo remove the following line
            // this.activity!!.selectImage(RC_SELECT_DP_IMAGE)
        }
    }

    @SuppressLint("InflateParams")
    private fun handleCustomMenuClicks() {

        pBtnEditAccount.setOnClickListener {
            editUserAccount()
        }

        pBtnAppSettings.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .addToBackStack("$this")
            val intent = Intent(context, AppSettings::class.java)
            if (intent.resolveActivity(context!!.packageManager) != null) {
                startActivity(intent)
            }
        }

        pBtnAboutDev.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .addToBackStack("$this")
            val intent = Intent(context, AboutDeveloper::class.java)
            if (intent.resolveActivity(context!!.packageManager) != null) {
                startActivity(intent)
            }

        }

        pBtnShareToOthers.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND, Uri.parse("mailto"))
            val body =
                "Hey, have you heard of Debts Manager ? I'ts an App heavily featured and capable " +
                        "of putting together your financial entities. I've been using it for a while now " +
                        "and its fun using the beautiful, smooth user interface that gives me the best user " +
                        "experince ever. Don't hesitate to  Download Debts Manager App from here " +
                        getString(R.string.download_link) + " Remember to share and rate it 5 star"

            shareIntent.apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, body)
                putExtra(Intent.EXTRA_SUBJECT, "Stay updated with new technology!")
            }
            try {
                startActivity(Intent.createChooser(shareIntent, "Share via ?"))

            } catch (exception: Exception) {
                toast(exception.toString())
            }
        }

        pBtnRate5Star.setOnClickListener {
            // show a rating dialog
            var rating = 5F // the default rating
            var feedBack: String
            val ratingBuilder = BottomSheetDialog(context!!)
            val ratingView = layoutInflater.inflate(R.layout.dialog_rate_app, null)

            ratingView.apply {
                ratingBar.setOnRatingBarChangeListener { ratingBar, _, _ ->
                    rating = ratingBar.rating
                }
                btnRate.setOnClickListener {
                    feedBack = etRatingComment.text.toString().trim()
                    val processedFeedBack =
                        if (feedBack.trim().isEmpty()) "no comment" else feedBack
                    if (rating < 1) {
                        ratingBar.rating = 1F
                        toast("rating can't be zero")
                    } else {
                        sendFeedback(
                            rating = rating,
                            message = processedFeedBack,
                            receiver = arrayOf("gacokieric@gmail.com")
                        )
                    }
                }

                collapseRatingView.setOnClickListener {
                    ratingBuilder.dismissWithAnimation = true
                    ratingBuilder.dismiss()
                }
            }

            ratingBuilder.apply {
                setContentView(ratingView)
                setCancelable(false)
                create()
                show()
            }
        }
    }

    private fun sendFeedback(rating: Float?, message: String, receiver: Array<String>?) {
        val displayUserName =
            activity!!.getSharedPreferences("userName", 0).getString("userName", "?")
        val sendingIntent = Intent(Intent.ACTION_SEND, Uri.parse("mailto"))
        sendingIntent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, receiver)
            putExtra(Intent.EXTRA_SUBJECT, "$displayUserName 's Debts Manager feedback")
            putExtra(Intent.EXTRA_TEXT, "< $rating stars > $message")
            putExtra(Intent.ACTION_ATTACH_DATA, "rateing")
        }
        try {
            startActivity(Intent.createChooser(sendingIntent, " Please select Email client "))
        } catch (exception: Exception) {
            toast(exception.toString())
        }

    }

    @Suppress("RemoveCurlyBracesFromTemplate")
    private fun editUserAccount() {

        val confirm: Boolean? =
            this@Profile.activity!!.userSharedPrefs("ShowConfirmDialog")
                ?.getBoolean("ShowConfirmDialog", true)

        if (confirm!!) {
            val confirmEditUserAccount = AlertDialog.Builder(this.context)
            confirmEditUserAccount.apply {
                setIcon(R.drawable.ic_edit)
                setTitle("Sure to Edit account ?")
                setMessage("Please note that you will not be able to change your name after 2 months")
                setHasOptionsMenu(true)
                setPositiveButton("Proceed") { _, _ ->
                    // don't show this dialog again
                    this@Profile.activity!!.userSharedPrefs("ShowConfirmDialog")?.edit()?.apply {
                        putBoolean("ShowConfirmDialog", false)
                        apply()
                    }
                    openEditAccount()
                }

                setNegativeButton("Cancel") { _, _ ->
                    // exit dialog
                }
                create().show()
            }
        } else openEditAccount()
    }

    private fun openEditAccount() {
        parentFragmentManager
            .beginTransaction()
            .addToBackStack("$this")

        val intent = Intent(context, EditUserAccount::class.java)
        if (intent.resolveActivity(activity!!.packageManager) != null) {
            startActivity(intent)
        }
    }


    @Suppress("SpellCheckingInspection")
    @SuppressLint("InflateParams")
    private fun deleteOrLogOut() {


        val delog = BottomSheetDialog(this.activity ?: this.activity!!)
        val delogView: View = layoutInflater.inflate(R.layout.dialog_delete_or_log_out, null)

        delogView.btnDelete.setOnClickListener {
            deleteAccount()
        }

        delogView.btnLogOut.setOnClickListener {
            logOut()
        }

        delog.apply {
            setContentView(delogView)
            create()
            show()
        }
    }

    private fun deleteAccount() {

        val confirmDelete = AlertDialog.Builder(this.context, 2)
        confirmDelete.apply {
            setIcon(R.drawable.ic_warning)
            setTitle("Delete account ?")
            setMessage("This action is potentially dangerous. Loss of your data is irreversible!")
            setPositiveButton("proceed") { _, _ ->
// todo check if user is connected to internet
                if (mUser != null) {
                    mUser!!.delete()
                        .addOnCompleteListener { delete ->
                            if (delete.isSuccessful) {
                                clearSharedPrefs("all")
                                startActivity(
                                    Intent(
                                        this.context,
                                        CreateAccountActivity::class.java
                                    )
                                )
                                toast("deleted successfully")
                            } else if (delete.isCanceled || !delete.isSuccessful) {
                                toast("failed to delete")
                            }
                        }
                } else {
                    toast("Account not registered!")
                }
            }

            setNegativeButton("cancel") { _, _ ->
                // close dialog
            }
            setCancelable(false)
            create().show()
        }

    }

    private fun logOut() {
        val confirmLogOut = AlertDialog.Builder(this.context, 3)
        confirmLogOut.apply {
            setTitle("Sure to log out ?")
            setIcon(R.drawable.ic_warning)
            setMessage("You can still sign in again.")
            setPositiveButton("proceed") { _, _ ->

                clearSharedPrefs("autoSignIn")
                startActivity(Intent(this.context, SignInActivity::class.java))
                toast("please sign in")
            }
            setNegativeButton("cancel") { _, _ ->
                // close dialog
            }
            setCancelable(false)
            create().show()
        }

    }

    @Suppress("LocalVariableName")
    private fun clearSharedPrefs(whichOne: String) {

        val autoSign: SharedPreferences? = activity!!.userSharedPrefs(AUTO_SIGN_IN)
        val autoSignEditor = autoSign?.edit()

        val hasAccount: SharedPreferences? = activity!!.userSharedPrefs(HAS_ACCOUNT, 0)
        val hasAccountEditor = hasAccount?.edit()

        val userName: SharedPreferences? = activity!!.userSharedPrefs(USER_NAME, 0)
        val userNameEditor = userName?.edit()

        if (whichOne == "all") {
            val prefsEditors = arrayOf(autoSignEditor, hasAccountEditor, userNameEditor)
            prefsEditors.forEach { editor: SharedPreferences.Editor? ->
                editor?.clear()?.apply()
            }
            return
        }
        if (whichOne == "autoSignIn") {
            autoSignEditor?.clear()?.apply()
            return
        }
    }
}