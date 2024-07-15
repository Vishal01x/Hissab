package com.exa.android.myapplication.Helper.Models

import com.google.firebase.crashlytics.internal.common.SystemCurrentTimeProvider

data class Pay(
    var id : String ? = null,
    var amount : Double = 0.0,
    var description : String? = "",
    var isRecieved : String? = "",
    var date : Long = System.currentTimeMillis()
)
