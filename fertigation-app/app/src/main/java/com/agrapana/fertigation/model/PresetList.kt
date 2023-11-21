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
    var fertigationDose: String = "",
    var irrigationDose: String = "",
)

data class ParameterPresetNow(
    var idealMoisture: String = "",
    var irrigationDays: String = "",
    var irrigationTimes: String = "",
    var fertigationDays: String = "",
    var fertigationTimes: String = "",
    var fertigationDose: String = "",
    var irrigationDose: String = "",
)

data class IntervalPreset(
    var userRequest: Int = 0,
    var systemRequest: Int = 0,
)