package com.agrapana.fertigation.ui.activity

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import com.agrapana.fertigation.model.AIInput
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.ui.fragment.CropRecommendationFragment
import com.agrapana.fertigation.ui.fragment.MainDeviceChartFragment
import com.agrapana.fertigation.ui.fragment.SeekPestsFragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import java.util.*


class DetailMainDeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMainDeviceBinding
    private lateinit var prefs: SharedPreferences
    private var noInternetDialog: NoInternetDialog? = null
    private var passedData: Field? = null

    private var pestPredictionResult: String? = null
    private var clientId: String? = null

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

        binding.pestWrapper.setOnClickListener {
            val dialog = SeekPestsFragment(pestPredictionResult)
            dialog.show(supportFragmentManager, "BottomSheetDialog")
        }

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Warmth"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Humidity"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Wind Speed"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Wind Pressure"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Light Intensity"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        replaceFragment(MainDeviceChartFragment(passedData!!.id!!, "Warmth"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> replaceFragment(MainDeviceChartFragment(passedData!!.id!!, "Warmth"))
                    1 -> replaceFragment(MainDeviceChartFragment(passedData!!.id!!, "Humidity"))
                    2 -> replaceFragment(MainDeviceChartFragment(passedData!!.id!!, "Wind Speed"))
                    3 -> replaceFragment(MainDeviceChartFragment(passedData!!.id!!, "Wind Pressure"))
                    4 -> replaceFragment(MainDeviceChartFragment(passedData!!.id!!, "Light Intensity"))
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun replaceFragment(fragment: Fragment?) {
        val fm = this.supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frame_container, fragment!!)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
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

    override fun onDestroy() {
//        if (mqttClient.isConnected()) {
//            mqttClient.destroy()
//        }
        super.onDestroy()
    }

}