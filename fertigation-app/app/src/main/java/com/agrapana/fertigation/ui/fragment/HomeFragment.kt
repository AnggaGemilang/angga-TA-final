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
import android.widget.Toast
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
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.ui.activity.LoginActivity
import com.agrapana.fertigation.ui.activity.SettingActivity
import com.agrapana.fertigation.ui.activity.WorkerActivity
import com.agrapana.fertigation.viewmodel.AuthViewModel
import com.agrapana.fertigation.viewmodel.FieldFilterViewModel
import com.agrapana.fertigation.viewmodel.FieldViewModel
import com.agrapana.fertigation.viewmodel.MonitorDeviceViewModel
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.agrapana.fertigation.viewmodel.PrimaryDeviceViewModel
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment: Fragment(), ChangeFieldListener, OperationListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var fieldAdapter: FieldFilterAdapter
    private lateinit var authViewModel: AuthViewModel
    private lateinit var fieldViewModel: FieldViewModel
    private lateinit var fieldFilterViewModel: FieldFilterViewModel
    private lateinit var presetViewModel: PresetViewModel
    private lateinit var primaryDeviceViewModel: PrimaryDeviceViewModel
    private lateinit var monitorDeviceViewModel: MonitorDeviceViewModel
    private lateinit var window: Window

    private var clientId: String? = null

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

        initRecyclerView()
        initViewModel()

        binding.toolbar.inflateMenu(R.menu.action_nav1)
        val role: String? = prefs.getString("client_role", "")
        if(role == "Worker"){
            binding.toolbar.menu.findItem(R.id.worker).isVisible = false
        }
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
                        authViewModel.logout(clientId!!)
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
                if(scrollY > 908){
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            })
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )
        binding.recyclerView.layoutManager = linearLayoutManager
        fieldAdapter = FieldFilterAdapter(activity!!)
        fieldAdapter.changeFieldListener = this
        binding.recyclerView.adapter = fieldAdapter
        fieldAdapter.notifyDataSetChanged()
    }

    private fun initViewModel() {
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.operationListener = this
        fieldViewModel = ViewModelProvider(this)[FieldViewModel::class.java]
        fieldViewModel.fetchFields(clientId!!)
        fieldViewModel.fields.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
                binding.dataFound.visibility = View.VISIBLE
                binding.dataNotFound.visibility = View.GONE
                fieldAdapter.setFieldList(it)
            } else {
                binding.dataFound.visibility = View.GONE
                binding.dataNotFound.visibility = View.VISIBLE
                val fieldList: ArrayList<Field> = ArrayList()
                fieldList.add(Field("Create Field Now", "N/A", 0, 0, "N/A", "N/A", 0, "N/A", false))
                fieldAdapter.setFieldList(fieldList)
                showMainField()
            }
            binding.valFilterFieldPlaceholder.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        fieldFilterViewModel = ViewModelProvider(this)[FieldFilterViewModel::class.java]
        fieldFilterViewModel.fields.observe(viewLifecycleOwner) { fields ->
            if (fields!!.isNotEmpty()) {
                binding.valFieldLoc.text = fields[0].address
            }
        }
        presetViewModel = ViewModelProvider(this)[PresetViewModel::class.java]
        presetViewModel.presets.observe(viewLifecycleOwner) {presets ->
            if (presets!!.isNotEmpty()) {
                binding.valPresetName.text = presets[0].presetName
                Glide.with(requireContext())
                    .load(presets[0].imageUrl)
                    .into(binding.valPresetImg)
            }
        }
        primaryDeviceViewModel = ViewModelProvider(this)[PrimaryDeviceViewModel::class.java]
        primaryDeviceViewModel.primaryDevices.observe(viewLifecycleOwner) { primaryDevice ->
            if (primaryDevice!!.isNotEmpty()) {
                binding.valWateringStatus.text = primaryDevice[0].wateringStatus
                binding.valFertilizingStatus.text = primaryDevice[0].fertilizingStatus
                binding.valFertilizerValve.text = primaryDevice[0].fertilizerValve
                binding.valWaterValve.text = primaryDevice[0].waterValve
                binding.valPump.text = primaryDevice[0].pumpStatus
                binding.valFertilizerTank.text = if (primaryDevice[0].waterTank != 0) primaryDevice[0].fertilizerTank.toString() + "%" else "N/A"
                binding.valWaterTank.text = if (primaryDevice[0].waterTank != 0) primaryDevice[0].waterTank.toString() + "%" else "N/A"
                binding.valDataTaken.text = "Taken at " + primaryDevice[0].takenAt
            }
        }
        monitorDeviceViewModel = ViewModelProvider(this)[MonitorDeviceViewModel::class.java]
        monitorDeviceViewModel.monitorDevices.observe(viewLifecycleOwner) { monitorDevice ->
            if (monitorDevice!!.isNotEmpty()) {
                var moisture = 0; var waterLevel = 0
                for(monitor in monitorDevice){
                    moisture += monitor.moisture
                    waterLevel += monitor.waterLevel
                }
                binding.valMoisture.text = if (monitorDevice[0].moisture != 0) (moisture / monitorDevice.size).toString() + "%" else "N/A"
                binding.valWaterLevel.text = if (monitorDevice[0].waterLevel != 0) (waterLevel / monitorDevice.size).toDouble().toString() else "N/A"
            }
            showMainField()
        }
    }

    override fun onChangeField(fieldId: String?, presetId: String?) {
        hideMainField()
        fieldFilterViewModel.fetchFieldsByWorker(clientId!!, fieldId!!)
        presetViewModel.fetchPresetById(clientId!!, presetId!!)
        primaryDeviceViewModel.fetchMonitoringData(clientId!!, fieldId)
        monitorDeviceViewModel.fetchMonitoringData(clientId!!, fieldId)
    }

    private fun hideMainField(){
        // Monitoring Atas
        binding.valWateringPlaceholder.visibility = View.VISIBLE
        binding.valPresetImgPlaceholder.visibility = View.VISIBLE
        binding.valPresetNamePlaceholder.visibility = View.VISIBLE
        binding.valFieldLocPlaceholder.visibility = View.VISIBLE
        binding.valFertilizingPlaceholder.visibility = View.VISIBLE
        binding.valPumpPlaceholder.visibility = View.VISIBLE
        binding.valWaterValvePlaceholder.visibility = View.VISIBLE
        binding.valFertilizerValvePlaceholder.visibility = View.VISIBLE
        binding.valTakenAtPlaceholder.visibility = View.VISIBLE
        binding.valWateringStatus.visibility = View.GONE
        binding.valFertilizingStatus.visibility = View.GONE
        binding.valPresetImg.visibility = View.GONE
        binding.valPump.visibility = View.GONE
        binding.valFieldLoc.visibility = View.GONE
        binding.valWaterValve.visibility = View.GONE
        binding.valFertilizerValve.visibility = View.GONE
        binding.valPresetName.visibility = View.GONE
        binding.valDataTaken.visibility = View.GONE

        // Monitoring Bawah

        binding.valWaterTankPlaceholder.visibility = View.VISIBLE
        binding.valFertilizerTankPlaceholder.visibility = View.VISIBLE
        binding.valMoisturePlaceholder.visibility = View.VISIBLE
        binding.valWaterLevel.visibility = View.VISIBLE
        binding.valWaterTank.visibility = View.GONE
        binding.valFertilizerTank.visibility = View.GONE
        binding.valWaterLevel.visibility = View.GONE
        binding.valMoisture.visibility = View.GONE
    }

    private fun showMainField(){

        // Monitoring Atas
        binding.valWateringPlaceholder.visibility = View.GONE
        binding.valPresetImgPlaceholder.visibility = View.GONE
        binding.valPresetNamePlaceholder.visibility = View.GONE
        binding.valFieldLocPlaceholder.visibility = View.GONE
        binding.valFertilizingPlaceholder.visibility = View.GONE
        binding.valTakenAtPlaceholder.visibility = View.GONE
        binding.valPumpPlaceholder.visibility = View.GONE
        binding.valWaterValvePlaceholder.visibility = View.GONE
        binding.valFertilizerValvePlaceholder.visibility = View.GONE
        binding.valWateringStatus.visibility = View.VISIBLE
        binding.valFertilizingStatus.visibility = View.VISIBLE
        binding.valPresetImg.visibility = View.VISIBLE
        binding.valPump.visibility = View.VISIBLE
        binding.valFieldLoc.visibility = View.VISIBLE
        binding.valWaterValve.visibility = View.VISIBLE
        binding.valFertilizerValve.visibility = View.VISIBLE
        binding.valPresetName.visibility = View.VISIBLE
        binding.valDataTaken.visibility = View.VISIBLE

        // Monitoring Bawah

        binding.valWaterTankPlaceholder.visibility = View.GONE
        binding.valFertilizerTankPlaceholder.visibility = View.GONE
        binding.valWaterLevel.visibility = View.GONE
        binding.valMoisturePlaceholder.visibility = View.GONE
        binding.valWaterTank.visibility = View.VISIBLE
        binding.valFertilizerTank.visibility = View.VISIBLE
        binding.valWaterLevel.visibility = View.VISIBLE
        binding.valMoisture.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onSuccess() {
        val editor: SharedPreferences.Editor? = prefs.edit()
        editor?.putBoolean("login_status", true)
        editor?.putString("client_id", null)
        editor?.putString("client_name", null)
        editor?.putString("client_role", null)
        val role: String? = prefs.getString("client_role", "")
        if(role == "Worker"){
            editor?.putString("worker_id", null)
            editor?.putString("worker_field_id", null)
        }
        editor?.apply()
        startActivity(Intent(activity, LoginActivity::class.java))
    }

    override fun onFailure(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }
}