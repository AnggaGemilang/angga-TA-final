package com.agrapana.fertigation.model

data class ParameterPreset(
    var id: String = "",
    var presetName: String = "",
    var imageUrl: String = "",
    var idealMoisture: Int = 0,
    var irrigationDays: Int = 0,
    var fertigationDays: Int = 0,
    var irrigationTimes: String = "",
    var fertigationTimes: String = "",
    var irrigationAge: String = "",
    var fertigationAge: String = "",
    var irrigationDoses: String = "",
    var fertigationDoses: String = "",
)

data class Controlling(
    var idealMoisture: Int = 0,
    var irrigationDays: Int = 0,
    var fertigationDays: Int = 0,
    var irrigationTimes: String = "",
    var fertigationTimes: String = "",
    var irrigationAge: String = "",
    var fertigationAge: String = "",
    var irrigationDoses: String = "",
    var fertigationDoses: String = "",
    var initialPlantAge: Int = 0,
    var createdAt: String = ""
)

data class IntervalPreset(
    var userRequest: Int = 0
)