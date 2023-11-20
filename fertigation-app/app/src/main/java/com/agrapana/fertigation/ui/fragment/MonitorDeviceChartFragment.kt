package com.agrapana.fertigation.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.FragmentSupportDeviceChartBinding
import com.agrapana.fertigation.helper.XAxisFormatter
import com.agrapana.fertigation.helper.YAxisFormatter
import com.agrapana.fertigation.viewmodel.DetailSupportDeviceViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.ArrayList

class MonitorDeviceChartFragment(
    private val id: String,
    private val number: Int,
    private val column: String,
) : Fragment() {

    private lateinit var binding: FragmentSupportDeviceChartBinding
    private lateinit var viewModel: DetailSupportDeviceViewModel
    private var type: String = "latest"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSupportDeviceChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.columnName.text = "$column Data"

        initViewModel()

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //
            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                type = parent?.getItemAtPosition(position).toString().lowercase()
                getChartData()
            }
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[DetailSupportDeviceViewModel::class.java]
        getChartData()
    }

    private fun getChartData(){

        lateinit var columnDB: String

        when(column) {
            "Warmth" -> {
                columnDB = "soil_temperature"
            }
            "Moisture" -> {
                columnDB = "soil_humidity"
            }
            "pH" -> {
                columnDB = "soil_ph"
            }
            "Nitrogen" -> {
                columnDB = "soil_nitrogen"
            }
            "Phosphor" -> {
                columnDB = "soil_phosphor"
            }
            "Kalium" -> {
                columnDB = "soil_kalium"
            }
        }
    }
}