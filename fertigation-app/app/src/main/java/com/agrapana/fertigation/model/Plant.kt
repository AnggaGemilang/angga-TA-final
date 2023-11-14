package com.agrapana.fertigation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    var id: String = "",
    var plantStarted: String = "",
    var plantEnded: String = "",
    var category: String = "",
    var mode: String = "",
    var plantType: String = "",
    var status: String = "",
    var imgUrl: String = ""
) : Parcelable
