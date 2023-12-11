package com.agrapana.fertigation.ui.activity

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.agrapana.fertigation.databinding.ActivityDetailSupportDeviceBinding
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.viewmodel.AuthViewModel
import com.agrapana.fertigation.viewmodel.FieldFilterViewModel
import com.agrapana.fertigation.viewmodel.FieldViewModel
import com.agrapana.fertigation.viewmodel.MonitorDeviceViewModel
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.agrapana.fertigation.viewmodel.PrimaryDeviceViewModel
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import java.util.*

class DetailMonitorDeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSupportDeviceBinding
    private lateinit var prefs: SharedPreferences
    private var noInternetDialog: NoInternetDialog? = null
    private lateinit var monitorDeviceViewModel: MonitorDeviceViewModel
    private var passedData: Field? = null
    private var identity: String? = null
    private var clientId: String? = null
    private var fieldId: String? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailSupportDeviceBinding.inflate(layoutInflater)
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
        fieldId = passedData!!.hardwareCode
        identity = intent.getStringExtra("identity")!!

        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = identity

        initViewModel()
    }

    private fun initViewModel() {
        monitorDeviceViewModel = ViewModelProvider(this)[MonitorDeviceViewModel::class.java]
        monitorDeviceViewModel.fetchMonitoringData(clientId!!, passedData!!.hardwareCode)
        monitorDeviceViewModel.monitorDevices.observe(this) { monitorDevice ->
            if (monitorDevice!!.isNotEmpty()) {
                monitorDevice.forEachIndexed { index, data ->
                    val identity = identity!!.split(" ")[2]
                    if(index+1 == identity.toInt()){
                        binding.valDeviceLocation.text = "Row $identity"
                        binding.valMoisture.text = data.moisture.toString() + "%"
                        binding.valWaterLevel.text = data.waterLevel.toString()
                    }
                }
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

    private fun hideMainField(){
        binding.valDeviceLocationPlaceholder.visibility = View.VISIBLE
        binding.valMoisturePlaceholder.visibility = View.VISIBLE
        binding.valWaterLevelPlaceholder.visibility = View.VISIBLE
        binding.valDeviceLocation.visibility = View.GONE
        binding.valWaterLevel.visibility = View.GONE
        binding.valMoisture.visibility = View.GONE
    }

    private fun showMainField(){
        binding.valDeviceLocationPlaceholder.visibility = View.GONE
        binding.valMoisturePlaceholder.visibility = View.GONE
        binding.valWaterLevelPlaceholder.visibility = View.GONE
        binding.valDeviceLocation.visibility = View.VISIBLE
        binding.valWaterLevel.visibility = View.VISIBLE
        binding.valMoisture.visibility = View.VISIBLE
    }

}