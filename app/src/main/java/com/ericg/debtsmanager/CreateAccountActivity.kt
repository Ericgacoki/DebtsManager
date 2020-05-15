package com.ericg.debtsmanager

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_account.*

private var mAuth: FirebaseAuth? = null
private var mUser: FirebaseUser? = null
private var fDataBase: FirebaseFirestore? = null

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_create_account)

        handleClicks()
    }

    override fun onStart() {
        super.onStart()
        initialise()
    }

    private fun initialise() {
        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth!!.currentUser
        fDataBase = FirebaseFirestore.getInstance()
    }

    private fun handleClicks() {
        btnLogIn.setOnClickListener {
            verifyInputs()
        }
    }

    private fun passwordIsStrong(): Boolean {
        var isStrong = false
        val userPassword = aPassword.text.toString().trim()
        val userConfirmPassword = aConfirmPassword.text.toString().trim()
        val passwords = arrayOf(aPassword, aConfirmPassword)

        if (userPassword == userConfirmPassword && userConfirmPassword.isNotEmpty()) {
            if (
            // check password's length

                (userPassword.trim().length >= 8)
                &&

                // Check if password contains special characters

                ((userPassword.contains("@", true)) ||
                        (userPassword.contains("_", true)) ||
                        (userPassword.contains("#", true)) ||
                        (userPassword.contains("$", true)) ||
                        (userPassword.contains("%", true)) ||
                        (userPassword.contains("&", true)) ||
                        (userPassword.contains("*", true)) ||
                        (userPassword.contains("!", true)) ||
                        (userPassword.contains("?", true)))
                &&

                //check if password contains at least one number

                ((userPassword.contains("0", true)) ||
                        (userPassword.contains("1", true)) ||
                        (userPassword.contains("2", true)) ||
                        (userPassword.contains("3", true)) ||
                        (userPassword.contains("4", true)) ||
                        (userPassword.contains("5", true)) ||
                        (userPassword.contains("6", true)) ||
                        (userPassword.contains("7", true)) ||
                        (userPassword.contains("8", true)) ||
                        (userPassword.contains("9", true)))

            ) {
                isStrong = true
            }

        } else if (userPassword != userConfirmPassword ) {
            aPassword.error = "not matching"
            aConfirmPassword.error = "not matching"
        }
        else {
            for(password in passwords){
                if(password.text.toString().trim().isEmpty()){
                    password.error = "${password.hint} is required"
                }
            }
        }
        return isStrong
    }

    private fun checkTheEmpty(items: Array<TextInputEditText>) {
        for (item in items){
            if (item.text.toString().trim().isEmpty()){
                item.error = "${item.hint} is required"
            }
        }
    }

    private fun verifyInputs() {

         val userName = aUserName.text.toString().trim()
         val userEmail = aEmail.text.toString().trim()
         val userPhone = aPhone.text.toString().trim()
         val userPassword = aPassword.text.toString().trim()
         val userConfirmPassword = aConfirmPassword.text.toString().trim()

         val notEmpty:Boolean = (userName.isNotEmpty() && userEmail.isNotEmpty() && userPhone.isNotEmpty()
                 && userPassword.isNotEmpty() && userConfirmPassword.isNotEmpty())

        //val inputs = arrayOf(userName, userEmail, userPhone, userPassword, userConfirmPassword)
        val inputsIds = arrayOf(aUserName, aEmail, aPhone, aPassword, aConfirmPassword)

        if (notEmpty && passwordIsStrong()) {
            // TODO create account,

            loadingStatus(true, btnEnabled = false)
            mAuth!!
                .createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        loadingStatus(false, btnEnabled = false)
                       // sendVerificationEmail()
                        startActivity(Intent(this@CreateAccountActivity, Debtors::class.java))
                        finish()
                    }
                    else if(task.isCanceled || !task.isSuccessful){
                        toast("An error occurred")
                        loadingStatus(false, btnEnabled = true)
                    }

                }

        }
        else if(!notEmpty){
            checkTheEmpty(inputsIds)
        }
        else if (!passwordIsStrong() && userPassword == userConfirmPassword){
            // TODO advice user and warn them for using weak password
            toast("Weak password!")
        }

    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, LENGTH_LONG).show()
    }
    private fun loadingStatus(showLoading: Boolean, btnEnabled: Boolean){

        if (showLoading){
        aLoadingView.apply {
            setViewColor(R.color.colorGreen)
            setRoundColor(R.color.colorOrange)
            startAnim()
            visibility = View.VISIBLE}
        }
        else{
            aLoadingView.apply {
                setViewColor(R.color.colorLightBgBlack)
                setRoundColor(R.color.colorLightBgBlack)
                stopAnim()
                visibility = View.INVISIBLE}
        }

        if(btnEnabled){
            buttonsStatus(true)}
        else{
            buttonsStatus(false)
        }

    }

     private fun buttonsStatus(enabled: Boolean){
         val buttons = arrayOf(btnLogIn, aBtnHelp, aBtnSignIn,aBtnIssue)
         for(button in buttons){
             button.isEnabled = enabled
         }
     }

    private fun sendVerificationEmail(){
        mUser!!
            .sendEmailVerification()
            .addOnCompleteListener {task ->
                if (task.isSuccessful){
                    toast("Link sent to ${mUser!!.email}")
                }
                else{
                    toast("Sending failed!")
                }

            }
    }

    val backIsEnabled = false
    override fun onBackPressed() = if (backIsEnabled) {
        super.onBackPressed()
    } else {
        toast("use on screen buttons instead")
    }
}