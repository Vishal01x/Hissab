package com.exa.android.myapplication.Helper.Models

data class Day(
    var id : String ? = null,
    val text : String = "",
    val amount : Double = 0.0
)

// An extension function use to convert day class to Map, since it is not neccesary to do so
// we can directly make an map and use like map[amount] = 122 as in C++
fun Day.toMap(): Map<String, Any> {
    val result = mutableMapOf<String, Any>()
    amount?.let { result["amount"] = it }
    return result
}
