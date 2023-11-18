package com.agrapana.fertigation.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class Preset(
    var id: String = "",
    var presetName: String = "",
    var imageUrl: String = "",
    var idealMoisture: String = "",
    var irrigationDays: String = "",
    var irrigationTimes: String = "",
    var fertigationDays: String = "",
    var fertigationTimes: String = "",
)