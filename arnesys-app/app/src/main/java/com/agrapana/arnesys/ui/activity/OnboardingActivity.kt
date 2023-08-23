package com.agrapana.arnesys.ui.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.agrapana.arnesys.R
import com.agrapana.arnesys.adapter.OnboardingAdapter
import com.agrapana.arnesys.data.OnboardingData
import com.agrapana.arnesys.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayout
import org.imaginativeworld.oopsnointernet.NoInternetDialog

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onBoardingAdapter: OnboardingAdapter
    private var noInternetDialog: NoInternetDialog? = null
    var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val onboardingData: MutableList<OnboardingData> = ArrayList()
        onboardingData.add(OnboardingData("Easy Life", "Make it easier for farmers to monitor soil conditions, weather, and pests on their agricultural land using arnesys",
            R.drawable.onboarding_1, 420, 420,  -50, 200, 0, 50))
        onboardingData.add(OnboardingData("Advance Tech", "Supported by technologies such as LoRA to support a centralized system, solar panels to meet electricity supply, and IoT for data communication",
            R.drawable.onboarding_2, 580, 580, -20, -20, 0, -200))
        onboardingData.add(OnboardingData("Best Quality", "Indirectly produce high quality plants by providing complete information about the current condition of the plants",
            R.drawable.onboarding_3, 380, 380, -20, 260, 0, 50))
        setOnBoardingViewPagerAdapter(onboardingData)

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = prefs.edit()
        editor?.putBoolean("firstStart", false)
        editor?.apply()

        binding.next.setOnClickListener {
            if(position < onboardingData.size-1){
                position++
                binding.slider.currentItem = position
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        binding.skip.setOnClickListener {
            if(position != onboardingData.size-1){
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                position = 0;
                binding.slider.currentItem = position
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                if(tab.position == onboardingData.size - 1){
                    binding.next.text = "Get Started"
                    binding.skip.text = "Back to Start"
                } else {
                    binding.next.text = "Next"
                    binding.skip.text = "Skip"
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    private fun setOnBoardingViewPagerAdapter(onboardingDataList: List<OnboardingData>){
        onBoardingAdapter = OnboardingAdapter(this, onboardingDataList)
        binding.slider.adapter = onBoardingAdapter
        binding.tabLayout.setupWithViewPager(binding.slider)
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