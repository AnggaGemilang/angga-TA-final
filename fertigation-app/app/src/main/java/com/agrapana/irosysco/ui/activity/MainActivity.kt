package com.agrapana.irosysco.ui.activity

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.agrapana.irosysco.R
import com.agrapana.irosysco.databinding.ActivityMainBinding
import com.agrapana.irosysco.ui.fragment.AddPlantFragment
import com.agrapana.irosysco.ui.fragment.HomeFragment
import com.agrapana.irosysco.ui.fragment.PlantListFragment
import com.agrapana.irosysco.ui.fragment.PresetFragment
import org.imaginativeworld.oopsnointernet.NoInternetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var noInternetDialog: NoInternetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        binding.bottomNavigationBar.background = null
        binding.bottomNavigationBar.menu.getItem(3).isEnabled = false

        binding.fab.setOnClickListener {
            AddPlantFragment().show(supportFragmentManager, "BottomSheetDialog")
        }

        val homeFragment = HomeFragment()
        makeCurrentFragment(homeFragment)

        val plantListFragment = PlantListFragment()
        val presetFragment = PresetFragment()

        binding.bottomNavigationBar.setOnNavigationItemSelectedListener() {
            when(it.itemId){
                R.id.home -> {
                    makeCurrentFragment(homeFragment)
                }
                R.id.lists -> {
                    makeCurrentFragment(plantListFragment)
                }
                R.id.presets -> {
                    makeCurrentFragment(presetFragment)
                }
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment).commit()
        }
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