package com.agrapana.fertigation.model

data class Field(
    var name: String = "",
    var address: String = "",
    var initialPlantAge: Int = 0,
    var landArea: Int = 0,
    var hardwareCode: String = "",
    var presetId: String = "",
    var numberOfMonitorDevice: Int = 0,
    var createdAt: String = "",
    var isExpandable: Boolean = false,
)