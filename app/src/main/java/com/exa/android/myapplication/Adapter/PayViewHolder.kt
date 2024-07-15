package com.exa.android.myapplication.Adapter

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.exa.android.myapplication.Helper.Models.Pay
import com.exa.android.myapplication.R
import com.exa.android.myapplication.databinding.PayNestedBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PayViewHolder(private val binding: PayNestedBinding,private val context : Context) : RecyclerView.ViewHolder(binding.root) {

    fun bind(data : Pay){

//        if (data.amount > 0) {
//            binding.amount.setTextColor(ContextCompat.getColor(context, R.color.errorColor))
//        }else{
//            binding.amount.setTextColor(ContextCompat.getColor(context, R.color.accentColor))
//        }

        binding.tag.text = data.isRecieved
        binding.amount.text = data.amount.toString()
        binding.date.text = formatDate(data.date)

        if(data.description?.isNotEmpty() == true) {
            binding.desc.text = data.description
            binding.desc.visibility = View.VISIBLE
        }
        if(data.isRecieved == "Sent"){
            binding.img.setImageResource(R.drawable.credite_to)
        }
    }

    fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd-M-yy", Locale.getDefault()) // Define your desired format pattern
        return formatter.format(date)
    }

}