package com.agrapana.fertigation.model

data class ParameterPreset(
    var id: String = "",
    var presetName: String = "",
    var imageUrl: String = "",
    var idealMoisture: String = "",
    var irrigationDays: String = "",
    var fertigationDays: String = "",
    var irrigationTimes: String = "",
    var fertigationTimes: String = "",
    var irrigationAge: String = "",
    var fertigationAge: String = "",
    var irrigationDose: String = "",
    var fertigationDose: String = "",
)

data class ParameterPresetNow(
    var idealMoisture: Int = 0,
    var irrigationDays: Int = 0,
    var fertigationDays: Int = 0,
    var irrigationTimes: String = "",
    var fertigationTimes: String = "",
    var irrigationAge: String = "",
    var fertigationAge: String = "",
    var irrigationDose: String = "",
    var fertigationDose: String = "",
)

data class IntervalPreset(
    var userRequest: Int = 0,
    var systemRequest: Int = 0,
)