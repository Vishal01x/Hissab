package com.exa.android.myapplication.Adapter

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.RecyclerView
import com.exa.android.myapplication.R

class DayViewHolder (val item : View) : RecyclerView.ViewHolder(item) {
    val date = item.findViewById<TextView>(R.id.date)
    val amount = item.findViewById<TextView>(R.id.total)
    val add_btn = item.findViewById<ImageView>(R.id.add)
    val drop_btn = item.findViewById<ImageView>(R.id.drop)
    val home = item.findViewById<ConstraintLayout>(R.id.home)
    val rec_parent = item.findViewById<LinearLayout>(R.id.home_rec_parent)
    val recycler_view = item.findViewById<RecyclerView>(R.id.home_item_recycler)
}