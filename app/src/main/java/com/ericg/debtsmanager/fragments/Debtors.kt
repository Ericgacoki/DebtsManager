/*
 * Copyright (c)  Updated by eric on  10/1/20 11:52 PM
 */

package com.ericg.debtsmanager.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ericg.debtsmanager.R
import com.ericg.debtsmanager.TOTAL_DEBTORS
import com.ericg.debtsmanager.adapters.DebtorsAdapter
import com.ericg.debtsmanager.data.DebtData
import com.ericg.debtsmanager.extensions.openAddDebtFragment
import com.ericg.debtsmanager.extensions.snackBuilder
import com.ericg.debtsmanager.extensions.toast
import com.ericg.debtsmanager.network.Internet.isConnected
import com.ericg.debtsmanager.utils.FirebaseUtils.mUser
import com.ericg.debtsmanager.utils.FirebaseUtils.userDataBase
import com.ericg.debtsmanager.viewmodel.GetDataViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.dialog_add_debt_payment.*
import kotlinx.android.synthetic.main.dialog_add_debt_payment.view.*
import kotlinx.android.synthetic.main.dialog_edit_debtor.*
import kotlinx.android.synthetic.main.dialog_edit_debtor.view.*
import kotlinx.android.synthetic.main.fragment_debtors.*

class Debtors : Fragment(), DebtorsAdapter.OnDebtorClickListener {

    private var debtorsList: List<DebtData> = ArrayList()

    private val debtorsAdapter =
        DebtorsAdapter(
            thisContext = null, debtorsList = debtorsList, listener = this
        )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_debtors, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
        onSwipeToRefresh()
        fabAddDebtor.setOnClickListener { openAddDebtFragment() }
        dataLoadingLayout1.setOnClickListener { /* do nothing */ }
    }

    private val retrieved: MutableLiveData<Boolean> = MutableLiveData(false)

    private fun loadData(
        load: Boolean? = true
    ): MutableLiveData<Boolean> {

        if (!isConnected()!!) {
            fabAddDebtor.snackBuilder("Its better when online !", 2000).apply {
                setTextColor(Color.WHITE)
                setBackgroundTint(Color.RED)
                setActionTextColor(Color.YELLOW)

                setAction("switch") {
                    // todo open internet connection settings
                }
                show()
            }
        }

        activateBtmNav(false)
        if (load!!) loadingBar(show = true) else loadingBar(false)
        noDebtorsYet.visibility = INVISIBLE

        ViewModelProvider(
            this,
            defaultViewModelProviderFactory
        ).get(GetDataViewModel::class.java)
            .getData("debtor")?.addOnCompleteListener { get ->
                if (get.isSuccessful) {
                    retrieved.value = true

                    activateBtmNav(true)
                    loadingBar(show = false)

                    debtorsList = get.result!!.toObjects(DebtData::class.java)
                    debtorsAdapter.debtorsList = debtorsList
                    debtorsAdapter.notifyDataSetChanged()

                    val numOfDebtors = debtorsList.size.toFloat()
                    noDebtorsYet.visibility = if (numOfDebtors.toInt() != 0) INVISIBLE else VISIBLE

                    activity!!.getSharedPreferences(TOTAL_DEBTORS, 0).edit()
                        .putFloat(TOTAL_DEBTORS, numOfDebtors).apply()
                } else {
                    activateBtmNav(true)
                    loadingBar(show = false)
                }
            }

        return retrieved
    }

    private fun loadingBar(show: Boolean) {
        if (show) {
            dataLoadingLayout1.visibility = View.VISIBLE
            loadingRing1.apply {
                setViewColor(ActivityCompat.getColor(this.context, R.color.colorLightBlue))
                startAnim()
            }
        } else {

            dataLoadingLayout1.visibility = View.INVISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        retrieved.value = false
    }

    private fun activateBtmNav(active: Boolean) {

        val navItems =
            arrayOf(
                R.id.debtors, R.id.myDebts, R.id.profile
                /*R.id.loans, R.id.installments*/,
                R.id.fabAddDebtor
            )
        if (!active) {

            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = false
                foundItem.isEnabled = false
            }
        } else {
            val debtors = activity!!.findViewById<View>(R.id.debtors)

            navItems.forEach { item ->
                val foundItem = activity!!.findViewById<View>(item)
                foundItem.isClickable = true
                foundItem.isEnabled = true
            }

            debtors.apply {
                isClickable = false
                isEnabled = false
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun onSwipeToRefresh() {
        swipeToRefreshDebtors.setOnRefreshListener {
            loadData(load = false).observe(viewLifecycleOwner, { retrieved ->
                if (retrieved) {
                    toast("refreshed successfully")
                    swipeToRefreshDebtors.isRefreshing = false
                }
            })
        }
    }

    private lateinit var newDate: String
    private fun showCalendar() {
        val calendarDialog = AlertDialog.Builder(this.context)
        val view = layoutInflater.inflate(R.layout.dialog_calendar, null)
        /*view.apply {
            pickNewDueDate.minDate = Calendar.getInstance().timeInMillis
            btnNewDueDateOk.setOnClickListener {
                newDate =
                    "${pickNewDueDate.dayOfMonth} /${pickNewDueDate.month} /${pickNewDueDate.year}"
                editDebtDueDate.setText(newDate)
               // calendarDialog.create().dismiss()
            }
        }*/
        calendarDialog.apply {
            setView(view)
            create()
            show()
        }
    }

    override fun onItemClick(itemView: View, itemViewId: Int, position: Int) {

        when (itemViewId) {
            R.id.deleteDebtor -> {
                val name = debtorsList[position].name
                val message: String =
                    if (debtorsList[position].initialAmt - debtorsList[position].amtPaid == 0) {
                        "It's safe to delete $name"
                    } else {
                        "$name hasn't cleared the debt yet!"
                    }
                val themeResId: Int =
                    if (debtorsList[position].initialAmt - debtorsList[position].amtPaid == 0) 3 else 1

                val deleteDebtorDialogBuilder = AlertDialog.Builder(this.context, themeResId)
                deleteDebtorDialogBuilder.apply {
                    setTitle("Sure to delete ?")
                    setIcon(
                        ContextCompat.getDrawable(
                            this@Debtors.context!!,
                            R.drawable.ic_warning
                        )
                    )
                    setMessage(message)
                    setPositiveButton("proceed") { _, _ ->
                        userDataBase?.collection("users/${mUser?.uid}/debtors")
                            ?.document(debtorsList[position].docID)?.delete()
                        loadData(true)

                        toast("you have deleted ${debtorsList[position].name}")

                    }
                    setNegativeButton("cancel") { _, _ ->
                        // cancel dialog
                    }
                    create().show()
                }
            }
            R.id.editDebtor -> {
                toast("edit the necessary details for ${debtorsList[position].name}")

                val name = debtorsList[position].name
                val phone = debtorsList[position].phone
                val currentDebt = debtorsList[position].remainingAmt
                val debtDueDate = debtorsList[position].dueDate

                val editDialogBuilder = BottomSheetDialog(this.context!!)
                val customEditDialogView =
                    layoutInflater.inflate(R.layout.dialog_edit_debtor, editDebtorRootLay)
                        .apply {
                            this.editDebtorName.setText(name)
                            editDebtorPhone.setText(phone)
                            editAddDebt.setText("")
                            editDebtDueDate.setOnClickListener {
                                showCalendar()
                            }
                            var canUpdate = true

                            saveChanges.setOnClickListener {
                                val documentReference = userDataBase
                                    ?.collection("users/${mUser?.uid}/debtors")
                                    ?.document(debtorsList[position].docID)

                                val eName: String
                                val ePhone: String
                                val eDebt: Int
                                val eDate: String

                                val nameAndPhoneNotEmpty =
                                    editDebtorName.text.toString().trim().isNotEmpty() &&
                                            editDebtorPhone.text.toString().trim().isNotEmpty()

                                val addDebt = editAddDebt.text.toString().trim().isNotEmpty()
                                val newDate = editDebtDueDate.text.toString().trim().isNotEmpty()

                                if (canUpdate) {

                                    if (nameAndPhoneNotEmpty && addDebt && newDate) {
                                        /* update all */

                                        eName = editDebtorName.text.toString()
                                        ePhone = editDebtorPhone.text.toString()
                                        eDebt = editAddDebt.text.toString().toInt()
                                        eDate = editDebtDueDate.text.toString()

                                        documentReference?.update(
                                            hashMapOf<String, Any>(
                                                "name" to eName,
                                                "phone" to ePhone,
                                                "dueDate" to eDate,
                                                "initialAmt" to (currentDebt!! + eDebt)
                                            )
                                        )?.addOnSuccessListener {
                                            canUpdate = false
                                            editDialogBuilder.dismiss()
                                        }

                                    } else if (nameAndPhoneNotEmpty && addDebt) {

                                        /* update name, phone, increment debt */

                                        eName = editDebtorName.text.toString()
                                        ePhone = editDebtorPhone.text.toString()
                                        eDebt = editAddDebt.text.toString().toInt()

                                        documentReference?.update(
                                            hashMapOf<String, Any>(
                                                "name" to eName,
                                                "phone" to ePhone,
                                                "initialAmt" to FieldValue.increment(eDebt.toLong())
                                            )
                                        )?.addOnSuccessListener {
                                            canUpdate = false
                                            editDialogBuilder.dismiss()
                                        }

                                    } else if (nameAndPhoneNotEmpty && newDate) {

                                        /* update name, phone, newDate */

                                        eName = editDebtorName.text.toString()
                                        ePhone = editDebtorPhone.text.toString()
                                        eDate = editDebtDueDate.text.toString()

                                        documentReference?.update(
                                            hashMapOf<String, Any>(
                                                "name" to eName,
                                                "phone" to ePhone,
                                                "dueDate" to eDate,
                                            )
                                        )?.addOnSuccessListener {
                                            canUpdate = false
                                            editDialogBuilder.dismiss()
                                        }

                                    } else if (nameAndPhoneNotEmpty) {

                                        canUpdate = false

                                        /* only update name and phone */

                                        eName = editDebtorName.text.toString()
                                        ePhone = editDebtorPhone.text.toString()

                                        documentReference?.update(
                                            hashMapOf<String, Any>(
                                                "name" to eName,
                                                "phone" to ePhone,
                                            )
                                        )?.addOnSuccessListener {
                                            canUpdate = false
                                            editDialogBuilder.dismiss()
                                        }

                                    } else {
                                        arrayOf(
                                            this.editDebtorName,
                                            this.editDebtorPhone
                                        ).forEach { input ->
                                            if (input.text.toString().trim().isEmpty()) {
                                                input.error = "${input.hint} can't be empty"
                                            }
                                        }
                                    }
                                } else {
                                    toast("you can only save once at a time")
                                    editDialogBuilder.dismiss()
                                }
                            }

                            cancelEditing.setOnClickListener {
                                editDialogBuilder.dismiss()
                            }
                        }
                editDialogBuilder.apply {
                    setOnDismissListener { loadData(true) }
                    setContentView(customEditDialogView)
                    setCancelable(false)
                    create()
                    show()
                }
            }

            R.id.debtStatus -> {
                val message = if (debtorsList[position].debtStatus == 1) "on time" else "too late"
                toast(message)
            }
            R.id.expandDebtorCard -> {

                /*if (itemView.expandableDebtorCardLayout!!.visibility == View.GONE) {
                    // delay card transition (in this case, expansion)
                    TransitionManager.beginDelayedTransition(debtorCard, AutoTransition())

                    itemView.expandableDebtorCardLayout!!.visibility = VISIBLE
                    itemView.expandDebtorCard.setImageDrawable(context?.let {
                        ContextCompat.getDrawable(it, R.drawable.ic_up) })
                    toast("expanded card  for ${debtorsList[position].name}")

                } else {
                    expandableDebtorCardLayout!!.visibility = View.GONE
                    expandDebtorCard.setImageDrawable(context?.let { context ->
                        ContextCompat.getDrawable(
                            context, R.drawable.ic_down) })
                    toast("collapsed card  for ${debtorsList[position].name}")
                }*/

            }

            R.id.callDebtor -> {
                toast("proceed to call  ${debtorsList[position].name}")
                val callIntent = Intent(
                    Intent.ACTION_CALL,
                    Uri.parse("tel:${debtorsList[position].phone}")
                )
                try {
                    this.context?.let { context ->
                        if (ActivityCompat.checkSelfPermission(
                                context, android.Manifest.permission.CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            startActivity(callIntent)
                        } else requestPermissions(
                            arrayOf(android.Manifest.permission.CALL_PHONE),
                            0
                        )
                    }
                } catch (e: Exception) {
                    toast(e.toString())
                }
            }

            R.id.debtPaymentProgress -> {
                val debtor = debtorsList[position].name
                val amtPaid = debtorsList[position].amtPaid
                toast("$debtor has  paid $amtPaid")
            }

            R.id.addDebtPayment -> {
                val remainingAmt = debtorsList[position].initialAmt - debtorsList[position].amtPaid
                val name = debtorsList[position].name
                var saved = false

                if (remainingAmt > 0) {
                    toast("add payment for $name")
                    val paymentBuilder = AlertDialog.Builder(this.context)
                    val paymentView = layoutInflater.inflate(R.layout.dialog_add_debt_payment, null)

                    paymentView.apply {
                        this.debtPaymentTitle.text = "Add payment for $name"

                        this.saveDebtPayment.setOnClickListener {

                            if (newPaymentAmt.text.toString().trim().isNotEmpty() &&
                                newPaymentAmt.text.toString().toInt() <= remainingAmt &&
                                newPaymentAmt.text.toString().toInt() > 0
                            ) {

                                val newPayedAmt = newPaymentAmt.text.toString().toInt()
                                val documentReference = userDataBase
                                    ?.collection("users/${mUser?.uid}/debtors")
                                    ?.document(debtorsList[position].docID)

                                /* save once to avoid overpayment or negative balance */
                                if (!saved) {
                                    documentReference?.update(
                                        hashMapOf<String, Any>(
                                            "amtPaid" to FieldValue.increment(newPayedAmt.toLong()),
                                            "paymentsDone" to FieldValue.increment(1L)
                                        )
                                    )?.addOnSuccessListener {
                                        paymentBuilder.create().dismiss()
                                    }
                                    saved = true

                                } else toast("you can only save once at a time")

                            } else if (newPaymentAmt.text.toString().isEmpty()) {
                                newPaymentAmt.error = "Can't be empty"
                            } else {
                                toast("enter amount between 1 and $remainingAmt inclusive")
                            }
                        }

                        eraseDebtPayment.setOnClickListener {
                            newPaymentAmt.setText("")
                        }
                    }

                    paymentBuilder.apply {
                        setNegativeButton("") { _, _ -> eraseDebtPayment.setOnClickListener { } }
                        setOnDismissListener { loadData(true) }
                        setView(paymentView)
                        create()
                        show()
                    }
                } else {
                    toast("This debt is cleared!")
                }
            }
        }
    }

    private fun updateUI() {
        loadData()
        debtorsRecyclerView.apply {
            adapter = debtorsAdapter
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            // scroll to bottom
            scrollToPosition(debtorsList.size - 1)
            setOnScrollChangeListener { _: View, _: Int, _: Int, _: Int, _: Int ->
                fabAddDebtor.startAnimation(
                    android.view.animation.AnimationUtils.loadAnimation(
                        context, R.anim.hide_on_scroll
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }
}