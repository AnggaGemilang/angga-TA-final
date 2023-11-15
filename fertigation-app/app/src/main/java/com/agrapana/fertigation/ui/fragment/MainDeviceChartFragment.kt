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
import com.agrapana.fertigation.databinding.FragmentMainDeviceChartBinding
import com.agrapana.fertigation.helper.XAxisFormatter
import com.agrapana.fertigation.helper.YAxisFormatter
import com.agrapana.fertigation.viewmodel.DetailMainDeviceViewModel
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class MainDeviceChartFragment(
    private val id: String,
    private val column: String,
    ) : Fragment() {

    private lateinit var binding: FragmentMainDeviceChartBinding
    private lateinit var viewModel: DetailMainDeviceViewModel
    private var type: String = "latest"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainDeviceChartBinding.inflate(inflater, container, false)
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
        viewModel = ViewModelProvider(this)[DetailMainDeviceViewModel::class.java]
        getChartData()
    }

    private fun getChartData(){

        lateinit var columnDB: String

        when(column) {
            "Weather" -> {
                columnDB = "wind_temperature"
            }
            "Pests" -> {
                columnDB = "wind_temperature"
            }
            "Warmth" -> {
                columnDB = "wind_temperature"
            }
            "Humidity" -> {
                columnDB = "wind_humidity"
            }
            "Wind Speed" -> {
                columnDB = "wind_speed"
            }
            "Wind Pressure" -> {
                columnDB = "wind_pressure"
            }
            "Light Intensity" -> {
                columnDB = "light_intensity"
            }
        }

        viewModel.getChartMainDevice(id, columnDB, type)
        viewModel.getLoadChartObservable().observe(activity!!) {
            binding.progressBar.visibility = View.GONE
            binding.content.visibility = View.VISIBLE
            val data = it?.data
            if(data != null){
                var lineDataSet: LineDataSet

                val dateData = ArrayList<String>()
                var lineList: ArrayList<Entry> = ArrayList()

                for((index, item) in data.withIndex()){
                    dateData.add(item.created_at.toString())
                    lineList.add(Entry(index.toFloat(), item.value!!.toFloat()))
                }

                lineDataSet = LineDataSet(lineList, "Histories of $column")
                lineDataSet.color = ContextCompat.getColor(requireContext(), R.color.green_40)
                lineDataSet.valueTextColor = Color.BLACK
                lineDataSet.valueTextSize = 14f
                lineDataSet.setDrawFilled(false)
                lineDataSet.setDrawValues(false)
                lineDataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.green_75))

                var lineData = LineData(lineDataSet)

                binding.apply {

                    val xAxis: XAxis = chart.xAxis
                    xAxis.labelRotationAngle = -45f;
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                    xAxis.valueFormatter = XAxisFormatter(dateData)

                    val yAxis = chart.axisLeft
                    yAxis.valueFormatter = YAxisFormatter()

                    val legend = chart.legend
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER;
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

                    chart.description.isEnabled = false
                    chart.data = lineData
                    chart.invalidate()
                }
            }
        }
    }
}