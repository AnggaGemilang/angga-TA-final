package com.agrapana.fertigation.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrapana.fertigation.*
import com.agrapana.fertigation.adapter.FieldFilterAdapter
import com.agrapana.fertigation.databinding.FragmentHomeBinding
import com.agrapana.fertigation.helper.ChangeFieldListener
import com.agrapana.fertigation.model.AIInput
import com.agrapana.fertigation.model.MonitoringSupportDevice
import com.agrapana.fertigation.model.Suggestion
import com.agrapana.fertigation.ui.activity.LoginActivity
import com.agrapana.fertigation.ui.activity.SettingActivity
import com.agrapana.fertigation.ui.activity.WorkerActivity
import com.agrapana.fertigation.viewmodel.FieldViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment: Fragment(), ChangeFieldListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var recyclerViewAdapter: FieldFilterAdapter
    private lateinit var viewModel: FieldViewModel
    private lateinit var window: Window

    private var messageSupportDevice: MonitoringSupportDevice? = null
    private var clientId: String? = null
    private var fieldId: String? = null
    private var pestPredictionResult: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        window = requireActivity().window
        prefs = this.activity?.getSharedPreferences("prefs",
            AppCompatActivity.MODE_PRIVATE
        )!!
        clientId = prefs.getString("client_id", "")

        val name: String? = prefs.getString("client_name", "")
        binding.greeting.text = "Hello there, $name"

        binding.btnPest.setOnClickListener {
            val dialog = SeekPestsFragment(pestPredictionResult)
            activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
        }

        binding.toolbar.inflateMenu(R.menu.action_nav1)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.worker -> {
                    startActivity(Intent(context, WorkerActivity::class.java))
                }
                R.id.about -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("App Version")
                        .setMessage("Beta 1.0.0")
                        .setCancelable(true)
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
                }
                R.id.setting -> {
                    startActivity(Intent(context, SettingActivity::class.java))
                }
                R.id.logout -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("You can't get in to your account")
                    builder.setPositiveButton("YES") { _, _ ->
                        val editor: SharedPreferences.Editor? = prefs.edit()
                        editor?.putBoolean("loginStart", true)
                        editor?.putString("client_id", null)
                        editor?.apply()
                        startActivity(Intent(activity, LoginActivity::class.java))
                    }
                    builder.setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }
            }
            true
        }

        val dtf = DateTimeFormatter.ofPattern("dd MMM")
        val localDate = LocalDate.now()
        binding.txtTanggalHome.text = dtf.format(localDate)

        binding.scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener {
                    _, _, scrollY, _, _ ->
                if(scrollY > 451){
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            })

        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )
        binding.recyclerView.layoutManager = linearLayoutManager
        recyclerViewAdapter = FieldFilterAdapter(activity!!)
        recyclerViewAdapter.changeFieldListener = this
        binding.recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[FieldViewModel::class.java]
    }

    override fun onChangeField(id: String?) {
        fieldId = id
    }

    private fun hideAdditionalField(){
        binding.valPest.visibility = View.GONE
        binding.valPestPlaceholder.visibility = View.VISIBLE
        binding.valWeather.visibility = View.GONE
        binding.valWeatherPlaceholder.visibility = View.VISIBLE
    }

    private fun showAdditionalField(){
        binding.valPestPlaceholder.visibility = View.GONE
        binding.valPest.visibility = View.VISIBLE
        binding.valWeatherPlaceholder.visibility = View.GONE
        binding.valWeather.visibility = View.VISIBLE
    }

    private fun hideMainField(){

        // Monitoring Atas

        binding.valWindTemperaturePlaceholder.visibility = View.VISIBLE
        binding.valWindHumidityPlaceholder.visibility = View.VISIBLE
        binding.valWindSpeedPlaceholder.visibility = View.VISIBLE
        binding.valPressurePlaceholder.visibility = View.VISIBLE
        binding.valLightPlaceholder.visibility = View.VISIBLE
        binding.valWindTemperature.visibility = View.GONE
        binding.valWindHumidity.visibility = View.GONE
        binding.valWindSpeed.visibility = View.GONE
        binding.valPressure.visibility = View.GONE
        binding.valLight.visibility = View.GONE

        // Monitoring Bawah

        binding.valTemperaturePlaceholder.visibility = View.VISIBLE
        binding.valSoilMoisturePlaceholder.visibility = View.VISIBLE
        binding.valSoilPhPlaceholder.visibility = View.VISIBLE
        binding.valSoilNitrogenPlaceholder.visibility = View.VISIBLE
        binding.valSoilPhosphorPlaceholder.visibility = View.VISIBLE
        binding.valSoilKaliumPlaceholder.visibility = View.VISIBLE
        binding.valSoilTemperature.visibility = View.GONE
        binding.valSoilMoisture.visibility = View.GONE
        binding.valSoilPh.visibility = View.GONE
        binding.valSoilNitrogen.visibility = View.GONE
        binding.valSoilPhosphor.visibility = View.GONE
        binding.valSoilKalium.visibility = View.GONE
    }

    private fun showMainField(){

        // Monitoring Atas

        binding.valWindTemperaturePlaceholder.visibility = View.GONE
        binding.valWindHumidityPlaceholder.visibility = View.GONE
        binding.valWindSpeedPlaceholder.visibility = View.GONE
        binding.valPressurePlaceholder.visibility = View.GONE
        binding.valLightPlaceholder.visibility = View.GONE
        binding.valWindTemperature.visibility = View.VISIBLE
        binding.valWindHumidity.visibility = View.VISIBLE
        binding.valWindSpeed.visibility = View.VISIBLE
        binding.valPressure.visibility = View.VISIBLE
        binding.valLight.visibility = View.VISIBLE

        // Monitoring Bawah

        binding.valTemperaturePlaceholder.visibility = View.GONE
        binding.valSoilMoisturePlaceholder.visibility = View.GONE
        binding.valSoilPhPlaceholder.visibility = View.GONE
        binding.valSoilNitrogenPlaceholder.visibility = View.GONE
        binding.valSoilPhosphorPlaceholder.visibility = View.GONE
        binding.valSoilKaliumPlaceholder.visibility = View.GONE
        binding.valSoilTemperature.visibility = View.VISIBLE
        binding.valSoilMoisture.visibility = View.VISIBLE
        binding.valSoilPh.visibility = View.VISIBLE
        binding.valSoilNitrogen.visibility = View.VISIBLE
        binding.valSoilPhosphor.visibility = View.VISIBLE
        binding.valSoilKalium.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
    }
}