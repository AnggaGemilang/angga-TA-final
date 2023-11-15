package com.agrapana.fertigation.helper

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.ArrayList

class XAxisFormatter(private var datas: ArrayList<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return datas[value.toInt()]
    }
}