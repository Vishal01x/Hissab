package com.exa.android.myapplication.Fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exa.android.myapplication.Adapter.DayViewHolder
import com.exa.android.myapplication.Adapter.ItemViewHolder
import com.exa.android.myapplication.Helper.Models.Day
import com.exa.android.myapplication.Helper.Models.Item
import com.exa.android.myapplication.Helper.SaveCallBack
import com.exa.android.myapplication.R
import com.exa.android.myapplication.databinding.FragmentHomeBinding
import com.exa.android.myapplication.databinding.ItemDialogBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference

    private var currentOpenedPosition : Int ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance()
        myRef = database.reference.child("Spends")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            showDatePickerDialog()
        }

        loadDays()
    }

    fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = "$selectedDay-${selectedMonth + 1}-$selectedYear"

                val ref = myRef.child("day").push()
                val refKey = ref.key
                var day : Day? = null
                if(refKey != null) {
                     day = Day(
                        refKey, selectedDate, 0.0
                    )
                }
                   day?.let {
                       ref.setValue(it)
                           .addOnSuccessListener {
                               Log.d("datePush", selectedDate.toString())
                           }
                           .addOnFailureListener { e ->
                               Log.e("datePushFailed", "error", e)
                           }
                   }
            },
            year, month, day
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    fun loadDays() {
        val dayRef = myRef.child("day")
        val options = FirebaseRecyclerOptions.Builder<Day>()
            .setQuery(dayRef, Day::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Day, DayViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
                return DayViewHolder(view)
            }

            override fun onBindViewHolder(holder: DayViewHolder, position: Int, model: Day) {
                holder.date.text = model.text
                holder.amount.text = model.amount.toString()
                val drop = holder.drop_btn

                loadItems(model,holder.recycler_view)

                holder.add_btn.setOnClickListener {
                    onClickAdd(model,holder)
                    //loadItems(model,holder.recycler_view)
//                    if(!holder.rec_parent.isVisible) {
//                        drop.setImageResource(R.drawable.arrow_up)
//                        holder.rec_parent.visibility = View.VISIBLE
//                    }
                   // currentOpenedPosition = position
                }
                holder.home.setOnClickListener{

                    if(holder.rec_parent.isVisible){
                        drop.setImageResource(R.drawable.drop_log)
                        holder.rec_parent.visibility = View.GONE
                       // currentOpenedPosition = null
                    }else {
                        drop.setImageResource(R.drawable.arrow_up)
                        holder.rec_parent.visibility = View.VISIBLE
                       // currentOpenedPosition = position
                       // loadItems(model,holder.recycler_view)
                    }
                }
               // holder.rec_parent.visibility = if(currentOpenedPosition == position)View.VISIBLE else View.GONE
                //drop.setImageResource(if(holder.rec_parent.isVisible)R.drawable.arrow_up else R.drawable.drop_log)
            }
        }

        binding.homeRecycle.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecycle.adapter = adapter
        adapter.startListening()

    }

    fun loadItems(day : Day, recyclerView: RecyclerView){
        val itemRef = myRef.child("items/${day.text}")
        val options = FirebaseRecyclerOptions.Builder<Item>()
            .setQuery(itemRef, Item::class.java)
            .build()

        val adapter = object : FirebaseRecyclerAdapter<Item,ItemViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item_nested,parent,false)
                return ItemViewHolder(view)
            }

            override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Item) {
                holder.name.text = model.itemName
                holder.desc.text = model.description
                holder.price.text = model.price.toString()
                holder.quantity.text = model.quantity.toString()

                if(model.description.isNotEmpty()){
                    holder.desc.visibility = View.VISIBLE
                }

                holder.edit.setOnClickListener{
                    onClickEdit(model,day)
                }
                holder.delete.setOnClickListener{
                    onClickDelete(model,day)
                }
            }

        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        adapter.startListening()
    }

    fun onClickAdd(day: Day, holder: DayViewHolder) {
        showDialog(day,object : SaveCallBack{
            override fun onSaveSuccess() {
                // handle after the data is added
            }

            override fun onSaveFailure() {
                Toast.makeText(requireContext(),
                    "Please check your Internet Connection", Toast.LENGTH_SHORT).show()
            }

        })
    }

    fun showDialog(day: Day, callback: SaveCallBack) {
        val sBinding = ItemDialogBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("New Entry")
            .setView(sBinding.root)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = sBinding.stockName.text.toString()
                val quantity = sBinding.quantity.text.toString()
                val price = sBinding.price.text.toString()
                val description = sBinding.description.text.toString()
                if (name.isEmpty()) {
                    sBinding.stockName.error = "Name is Required"
                } else if (quantity.isEmpty()) {
                    sBinding.quantity.error = "Quantity is Required"
                } else if (price.isEmpty()) {
                    sBinding.price.error = "Price is Required"
                } else {
                    val quantityDouble = quantity.toDouble()
                    val priceDouble = price.toDouble()
                    val item = Item(
                        quantity = quantityDouble,
                        price = priceDouble,
                        itemName = name,
                        description = description
                    )

                    Log.d("HomeFragment data", item.toString())
                    saveInDatabase(day, item,onSuccess = {
                        dialog.dismiss()
                        callback.onSaveSuccess()
                    },onFailure = {
                      callback.onSaveFailure()
                    })
                }
            }
        }

        dialog.show()
    }

    fun onClickEdit(item: Item, day: Day) {
        val sBinding = ItemDialogBinding.inflate(layoutInflater)
        sBinding.stockName.text = Editable.Factory.getInstance().newEditable(item.itemName)
        sBinding.price.text = Editable.Factory.getInstance().newEditable(item.price.toString())
        sBinding.quantity.text = Editable.Factory.getInstance().newEditable(item.quantity.toString())
        sBinding.description.text = Editable.Factory.getInstance().newEditable(item.description)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Item")
            .setView(sBinding.root)
            .setPositiveButton("Edit"){dialog,_ ->
                val quantity = sBinding.quantity.text.toString().toDoubleOrNull() ?: 0.0
                val price = sBinding.price.text.toString().toDoubleOrNull() ?: 0.0
                val name = sBinding.stockName.text.toString()
                val desc = sBinding.description.text.toString()

                val updatedItem = Item(
                    quantity = quantity,
                    price = price,
                    itemName = name,
                    description = desc
                )

                val updateData = mapOf(
                    "quantity" to updatedItem.quantity,
                    "price" to updatedItem.price,
                    "itemName" to updatedItem.itemName,
                    "description" to updatedItem.description
                )

                myRef.child("items/${day.text}/${item.id}").updateChildren(updateData)
                    .addOnSuccessListener {
                        Log.d("HomeFragment", "updateItemPush")
                        updateAmount(day,updatedItem.price-item.price)
                    }
                    .addOnFailureListener {
                        Log.d("HomeFragment", "updateFailde")
                    }

                // add delete this data from database and then add to database
                dialog.dismiss()
            }
            .setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    fun onClickDelete(item: Item, day: Day){
        myRef.child("items/${day.text}/${item.id}").removeValue()
            .addOnSuccessListener {
                updateAmount(day,item.price,true)
            }
    }

    fun updateAmount(day: Day, price : Double, isDelete : Boolean = false){
        var newAmount = day.amount
        if(isDelete){
            newAmount -= price
        }else{
            newAmount += price
        }

        val updatedData = mapOf(
            "date" to day.text,
            "amount" to newAmount
        )
        myRef.child("day/${day.id}").updateChildren(updatedData)
            .addOnSuccessListener {
                Log.d("home fragment", "amount is updated")
            }
            .addOnFailureListener {
                Log.d("home Fragment", "amount updation is failed")
            }
    }

    fun saveInDatabase(d: Day, item: Item,onSuccess : ()->Unit, onFailure: ()->Unit){
        val ref = myRef.child("items/${d.text}").push()
        val dataKey = ref.key
        item.id = dataKey

        ref.setValue(item)
            .addOnSuccessListener {
                Log.d("itemPush", item.toString())
                updateAmount(d,item.price)
                 onSuccess()
            }
            .addOnFailureListener {
                Log.e("itemPushFailed", "error", it)
                onFailure()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
