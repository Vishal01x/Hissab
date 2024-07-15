package com.exa.android.myapplication.Adapter

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exa.android.myapplication.R

class ItemViewHolder(val item : View) : RecyclerView.ViewHolder(item) {
    val name = item.findViewById<TextView>(R.id.item_name)
    val price = item.findViewById<TextView>(R.id.price)
    val quantity = item.findViewById<TextView>(R.id.quantity)
    val desc = item.findViewById<TextView>(R.id.description)
    val edit = item.findViewById<ImageView>(R.id.edit)
    val delete = item.findViewById<ImageView>(R.id.delete)
}