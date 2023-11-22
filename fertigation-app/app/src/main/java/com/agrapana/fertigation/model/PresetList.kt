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
    var idealMoisture: Int = 0,
    var irrigationDays: Int = 0,
    var irrigationTimes: String = "",
    var fertigationDays: Int = 0,
    var fertigationTimes: String = "",
    var fertigationDose: Int = 0,
    var irrigationDose: Int = 0,
)

data class IntervalPreset(
    var userRequest: Int = 0,
    var systemRequest: Int = 0,
)