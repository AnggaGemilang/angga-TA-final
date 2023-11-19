package com.agrapana.fertigation.model

data class ParameterPreset(
    var id: String = "",
    var presetName: String = "",
    var imageUrl: String = "",
    var idealMoisture: String = "",
    var irrigationDays: String = "",
    var irrigationTimes: String = "",
    var fertigationDays: String = "",
    var fertigationTimes: String = "",
)

data class IntervalPreset(
    var userReqest: Int = 0,
    var systemRequest: Int = 0,
)