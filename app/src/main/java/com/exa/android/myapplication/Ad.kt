package com.exa.android.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class Ad(val data : List<String>) : Adapter<Ad.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Ad.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder : ViewHolder, position: Int) {
       holder.date.text = data[position]
    }

    class ViewHolder(val item : View) : RecyclerView.ViewHolder(item) {
       val date = item.findViewById<TextView>(R.id.date)
       val btn = item.findViewById<ImageButton>(R.id.add)

    }

}