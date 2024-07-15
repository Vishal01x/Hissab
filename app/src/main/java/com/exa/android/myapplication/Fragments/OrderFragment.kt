package com.exa.android.myapplication.Fragments

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exa.android.myapplication.Adapter.DayViewHolder
import com.exa.android.myapplication.Adapter.PayViewHolder
import com.exa.android.myapplication.Helper.Models.Day
import com.exa.android.myapplication.Helper.Models.Pay
import com.exa.android.myapplication.Helper.Models.toMap
import com.exa.android.myapplication.R
import com.exa.android.myapplication.databinding.FragmentHomeBinding
import com.exa.android.myapplication.databinding.PayDialogBinding
import com.exa.android.myapplication.databinding.PayItemBinding
import com.exa.android.myapplication.databinding.PayNestedBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.absoluteValue

class OrderFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference

    private val TAG = "Order Fragment"
    private var isAdded = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference.child("Transaction")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fab.setOnClickListener {
            showDialog()
        }
        loadPay()
    }

    private fun showDialog() {
        val binding = PayDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Member Transaction")
            .setView(binding.root)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = binding.name.text.toString()
                if (TextUtils.isEmpty(name)) {
                    binding.name.error = "Name is Required"
                } else {
                    val ref = myRef.child("pay").push()
                    val key = ref.key
                    val pay = Day(id = key, text = name, amount = 0.0)
                    ref.setValue(pay)
                        .addOnSuccessListener {
                            Log.d("TAG", pay.toString())
                        }
                        .addOnFailureListener { e ->
                            Log.e("TAG", "error", e)
                        }
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun loadPay() {
        val payRef = myRef.child("pay")
        val options = FirebaseRecyclerOptions.Builder<Day>()
            .setQuery(payRef, Day::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Day, DayViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
                return DayViewHolder(view)
            }

            override fun onBindViewHolder(holder: DayViewHolder, position: Int, model: Day) {

                if (model.amount < 0) {
                    holder.amount.setTextColor(ContextCompat.getColor(requireContext(), R.color.errorColor))
                } else {
                    holder.amount.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.accentColor
                        )
                    )
                }

                holder.date.text = model.text
                holder.amount.text = model.amount.absoluteValue.toString()
                val drop = holder.drop_btn

                loadTransaction(model, holder.recycler_view)

                holder.add_btn.setOnClickListener {

                    if (isAdded) {
                        // Scroll to the bottom of the nested RecyclerView

                            holder.recycler_view.smoothScrollToPosition(
                                holder.recycler_view.adapter?.itemCount ?: (0 - 1)
                            )

                        setupAddButton(holder, model)
                    }
                    else {
                        Toast.makeText(
                            requireContext(),
                            "Firstly add the previous field",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                holder.home.setOnClickListener {
                    if (holder.rec_parent.isVisible) {
                        drop.setImageResource(R.drawable.drop_log)
                        holder.rec_parent.visibility = View.GONE
                    } else {
                        drop.setImageResource(R.drawable.arrow_up)
                        holder.rec_parent.visibility = View.VISIBLE
                    }
                }
            }
        }

        binding.homeRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecycle.adapter = adapter
        adapter.startListening()
    }

    private fun setupAddButton(holder: DayViewHolder, pay: Day) {
        isAdded = false
        val inflater = LayoutInflater.from(holder.itemView.context)
        val newEntryView = inflater.inflate(R.layout.pay_item, holder.itemView as ViewGroup, false)

        // Find the container to add the new entry view
        val container = holder.itemView.findViewById<LinearLayout>(R.id.pay_item_container)
        container.addView(newEntryView)

        val addBtn = newEntryView.findViewById<Button>(R.id.add)
        val spinner = newEntryView.findViewById<LinearLayout>(R.id.spinner)
        var isReceived: String? = null

        val items = arrayOf("Received", "Sent")

        spinner.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Select an item")
            builder.setItems(items) { dialog, which ->
                // Handle item selection
                Log.d(TAG, items[which])
                if (items[which] == "Received") isReceived = "Received"
                else isReceived = "Sent"
            }
            builder.show()
        }

        addBtn.setOnClickListener {
            val amountText =
                newEntryView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.amount).text.toString()
            val descText =
                newEntryView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.desc).text.toString()

            if (amountText.isEmpty()) {
                newEntryView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.amount).error =
                    "Amount is required"
            } else if (isReceived == null) {
                Toast.makeText(
                    requireContext(),
                    "Please select transaction type",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val data = Pay(
                    amount = amountText.toDouble(),
                    description = descText,
                    isRecieved = isReceived
                )
                val ref = myRef.child("pay/${pay.id}/${pay.text}").push()
                val key = ref.key
                data.id = key
                ref.setValue(data)
                    .addOnSuccessListener {
                        Log.d(TAG, data.toString())
                        isAdded = true
                        newEntryView.visibility = View.GONE
                        updateAmount(pay, data)

                        // Scroll to the bottom of the RecyclerView
//                          // Scroll to the bottom of the nested RecyclerView
                    holder.recycler_view.post {
                        holder.recycler_view.smoothScrollToPosition(holder.recycler_view.adapter?.itemCount ?: 0 - 1)
                    }
                    }
                    .addOnFailureListener {
                        Log.e(TAG, "error", it)
                    }
            }
        }
    }

    fun loadTransaction(pay: Day, recycleView: RecyclerView) {
        val ref = myRef.child("pay/${pay.id}/${pay.text}")
        Log.d(TAG, "key - $ref")
        val options = FirebaseRecyclerOptions.Builder<Pay>()
            .setQuery(ref, Pay::class.java)
            .build()

        Log.d(TAG, options.toString())
        val adapter = object : FirebaseRecyclerAdapter<Pay, PayViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayViewHolder {
                val bindingPay = PayNestedBinding.inflate(layoutInflater, parent, false)
                return PayViewHolder(bindingPay, parent.context)
            }

            override fun onBindViewHolder(holder: PayViewHolder, position: Int, model: Pay) {
                holder.bind(model)
            }

        }

        recycleView.layoutManager = LinearLayoutManager(requireContext())
        recycleView.adapter = adapter
        adapter.startListening()
    }

    fun updateAmount(pay: Day, data: Pay) {

        var newAmount = 0.0

        if (data.isRecieved == "Sent") {
            newAmount = pay.amount + data.amount
        } else {
            newAmount = pay.amount - data.amount
        }

        val ref = myRef.child("pay/${pay.id}")

        val updatePay = Day(amount = newAmount)
        val updateMap = updatePay.toMap()

        ref.updateChildren(updateMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FirebaseUpdate", "Amount updated successfully.")
            } else {
                Log.e("FirebaseUpdate", "Failed to update amount.", task.exception)
            }
        }
        /* updateChildren method requires only those attributes that you want to update only no need
         * no Need to send whole data the same above can also be directly achieved using direct declaration
         * val updateMap = mutableMapOf<String, Any?>()
         * updateMap["amount"] = 150.0
         */
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
