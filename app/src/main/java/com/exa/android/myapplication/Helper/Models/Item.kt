package com.exa.android.myapplication.Helper.Models

data class Item(
    var id :String? = null,
    val itemName : String = "",
    val price : Double = 0.0,
    val quantity : Double = 0.0,
    val description : String = ""
    )
