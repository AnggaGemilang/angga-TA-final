package com.agrapana.fertigation.ui.activity

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.ActivityDetailSupportDeviceBinding
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.ui.fragment.SupportDeviceChartFragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import java.util.*

class DetailSupportDeviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailSupportDeviceBinding
    private lateinit var prefs: SharedPreferences
    private var noInternetDialog: NoInternetDialog? = null

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

        val passedData: Field = Gson().fromJson(intent.getStringExtra("passData"), Field::class.java)
        fieldId = passedData.id

        val identity: String = intent.getStringExtra("identity")!!

        val imageParts = passedData.thumbnail.toString().trim().split("public/".toRegex())
        if(passedData.thumbnail != null){
            Glide.with(this).load("https://arnesys.agrapana.tech/storage/" + imageParts[1]).into(binding.imgThumbnail);
        } else {
            Glide.with(this).load(R.drawable.farmland).into(binding.imgThumbnail);
        }

        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = identity

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Warmth"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Moisture"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("pH"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Nitrogen"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Phosphor"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Kalium"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        replaceFragment(SupportDeviceChartFragment(passedData.id!!, 1, "Warmth"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> replaceFragment(SupportDeviceChartFragment(passedData.id, 1,"Warmth"))
                    1 -> replaceFragment(SupportDeviceChartFragment(passedData.id, 1,"Moisture"))
                    2 -> replaceFragment(SupportDeviceChartFragment(passedData.id, 1,"pH"))
                    3 -> replaceFragment(SupportDeviceChartFragment(passedData.id, 1,"Nitrogen"))
                    4 -> replaceFragment(SupportDeviceChartFragment(passedData.id, 1,"Phosphor"))
                    5 -> replaceFragment(SupportDeviceChartFragment(passedData.id, 1,"Kalium"))
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
        menuInflater.inflate(R.menu.action_nav4, menu)
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

}