package com.agrapana.arnesys.helper

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class YAxisFormatter : ValueFormatter() {
    private val format = DecimalFormat("###,##0.0")
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return format.format(value)
    }
}