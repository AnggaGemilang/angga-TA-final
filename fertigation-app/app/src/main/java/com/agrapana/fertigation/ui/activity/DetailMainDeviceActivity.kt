package com.agrapana.fertigation.ui.activity

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.ActivityDetailMainDeviceBinding
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.viewmodel.FieldFilterViewModel
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.agrapana.fertigation.viewmodel.PrimaryDeviceViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import java.util.*


class DetailMainDeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMainDeviceBinding
    private lateinit var prefs: SharedPreferences
    private var noInternetDialog: NoInternetDialog? = null
    private var passedData: Field? = null
    private var clientId: String? = null
    private lateinit var fieldFilterViewModel: FieldFilterViewModel
    private lateinit var presetViewModel: PresetViewModel
    private lateinit var primaryDeviceViewModel: PrimaryDeviceViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMainDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        prefs = this.getSharedPreferences("prefs", MODE_PRIVATE)!!
        clientId = prefs.getString("client_id", "")

        passedData = Gson().fromJson(intent.getStringExtra("passData"), Field::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Main Device"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initViewModel()

    }

    private fun initViewModel() {
        fieldFilterViewModel = ViewModelProvider(this)[FieldFilterViewModel::class.java]
        fieldFilterViewModel.fetchFieldsByWorker(clientId!!, passedData!!.hardwareCode)
        fieldFilterViewModel.fields.observe(this) { fields ->
            if (fields!!.isNotEmpty()) {
                binding.valFieldLoc.text = fields[0].address
            }
        }
        presetViewModel = ViewModelProvider(this)[PresetViewModel::class.java]
        presetViewModel.fetchPresetById(clientId!!, passedData!!.presetId)
        presetViewModel.presets.observe(this) {presets ->
            if (presets!!.isNotEmpty()) {
                binding.valPresetName.text = presets[0].presetName
            }
        }
        primaryDeviceViewModel = ViewModelProvider(this)[PrimaryDeviceViewModel::class.java]
        primaryDeviceViewModel.fetchMonitoringData(clientId!!, passedData!!.hardwareCode)
        primaryDeviceViewModel.primaryDevices.observe(this) { primaryDevice ->
            if (primaryDevice!!.isNotEmpty()) {
                binding.valWateringStatus.text = primaryDevice[0].wateringStatus
                binding.valFertilizingStatus.text = primaryDevice[0].fertilizingStatus
                binding.valFertilizerValveStatus.text = primaryDevice[0].fertilizerValve
                binding.valWaterValveStatus.text = primaryDevice[0].waterValve
                binding.valPumpStatus.text = primaryDevice[0].pumpStatus
                binding.valFertilizerTank.text = primaryDevice[0].fertilizerTank.toString() + "%"
                binding.valWaterTank.text = primaryDevice[0].waterTank.toString() + "%"
                binding.valTakenAt.text = primaryDevice[0].takenAt
            }
            showMainField()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_nav3, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about -> {
                AlertDialog.Builder(this)
                    .setTitle("App Version")
                    .setMessage("Beta 1.0.0")
                    .setCancelable(true)
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        if (noInternetDialog != null) {
            noInternetDialog!!.destroy();
        }
    }

    private fun hideMainField(){
        binding.valWateringStatusPlaceholder.visibility = View.VISIBLE
        binding.valPresetNamePlaceholder.visibility = View.VISIBLE
        binding.valFieldLocPlaceholder.visibility = View.VISIBLE
        binding.valFertilizingStatusPlaceholder.visibility = View.VISIBLE
        binding.valPumpStatusPlaceholder.visibility = View.VISIBLE
        binding.valWaterTankPlaceholder.visibility = View.VISIBLE
        binding.valFertilizerTankPlaceholder.visibility = View.VISIBLE
        binding.valWaterValveStatusPlaceholder.visibility = View.VISIBLE
        binding.valFertilizerValveStatusPlaceholder.visibility = View.VISIBLE
        binding.valTakenAtPlaceholder.visibility = View.VISIBLE
        binding.valWateringStatus.visibility = View.GONE
        binding.valFertilizingStatus.visibility = View.GONE
        binding.valPumpStatus.visibility = View.GONE
        binding.valWaterTank.visibility = View.GONE
        binding.valFertilizerTank.visibility = View.GONE
        binding.valFieldLoc.visibility = View.GONE
        binding.valWaterValveStatus.visibility = View.GONE
        binding.valFertilizerValveStatus.visibility = View.GONE
        binding.valPresetName.visibility = View.GONE
        binding.valTakenAt.visibility = View.GONE
    }

    private fun showMainField(){
        binding.valWateringStatusPlaceholder.visibility = View.GONE
        binding.valPresetNamePlaceholder.visibility = View.GONE
        binding.valFieldLocPlaceholder.visibility = View.GONE
        binding.valFertilizingStatusPlaceholder.visibility = View.GONE
        binding.valTakenAtPlaceholder.visibility = View.GONE
        binding.valWaterTankPlaceholder.visibility = View.GONE
        binding.valFertilizerTankPlaceholder.visibility = View.GONE
        binding.valPumpStatusPlaceholder.visibility = View.GONE
        binding.valWaterValveStatusPlaceholder.visibility = View.GONE
        binding.valFertilizerValveStatusPlaceholder.visibility = View.GONE
        binding.valWateringStatus.visibility = View.VISIBLE
        binding.valFertilizingStatus.visibility = View.VISIBLE
        binding.valPumpStatus.visibility = View.VISIBLE
        binding.valFertilizerTank.visibility = View.VISIBLE
        binding.valWaterTank.visibility = View.VISIBLE
        binding.valFieldLoc.visibility = View.VISIBLE
        binding.valWaterValveStatus.visibility = View.VISIBLE
        binding.valFertilizerValveStatus.visibility = View.VISIBLE
        binding.valPresetName.visibility = View.VISIBLE
        binding.valTakenAt.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        val builder1 = NoInternetDialog.Builder(this)
        builder1.cancelable = false // Optional
        builder1.noInternetConnectionTitle = "No Internet" // Optional
        builder1.noInternetConnectionMessage = "Check your Internet connection and try again" // Optional
        builder1.showInternetOnButtons = true // Optional
        builder1.pleaseTurnOnText = "Please turn on" // Optional
        builder1.wifiOnButtonText = "Wifi" // Optional
        builder1.mobileDataOnButtonText = "Mobile data" // Optional
        builder1.onAirplaneModeTitle = "No Internet" // Optional
        builder1.onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
        builder1.pleaseTurnOffText = "Please turn off" // Optional
        builder1.airplaneModeOffButtonText = "Airplane mode" // Optional
        builder1.showAirplaneModeOffButtons = true // Optional
        noInternetDialog = builder1.build()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}