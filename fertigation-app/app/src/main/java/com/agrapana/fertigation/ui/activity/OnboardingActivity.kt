package com.agrapana.fertigation.ui.activity

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.OnboardingAdapter
import com.agrapana.fertigation.data.OnboardingData
import com.agrapana.fertigation.databinding.ActivityOnboardingBinding
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

        if (Build.VERSION.SDK_INT >= 32) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) return
            val launcher = registerForActivityResult<String, Boolean>(
                ActivityResultContracts.RequestPermission()
            ) { _: Boolean? -> }
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        val onboardingData: MutableList<OnboardingData> = ArrayList()
        onboardingData.add(OnboardingData("Mudahkan Hidup", "Sistem ini membuat kegiatan penyiraman dan pemupukan dapat dilakukan secara efisien, dengan memungkinkan pemantauan dan pengendalian jarak jauh",
            R.drawable.onboarding_4, 420, 420,  0, 200, 0, 50))
        onboardingData.add(OnboardingData("Teknologi Canggih", "Sistem ini memungkinkan penggunaan preset secara dinamis dengan parameter yang dapat petani atur, didukung dengan teknologi yang murah dan berkualitas",
            R.drawable.onboarding_2, 580, 580, -20, -20, 0, -200))
        onboardingData.add(OnboardingData("Kebermanfaatan", "Pengembangan sistem dilakukan berdasarkan proses observasi yang panjang terhadap narasumber dan literatur, sehingga menciptakan kesesuaian penerapan",
            R.drawable.onboarding_5, 380, 380, -20, 260, 0, 50))
        setOnBoardingViewPagerAdapter(onboardingData)

        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val editor: SharedPreferences.Editor? = prefs.edit()
        editor?.putBoolean("first_start_status", false)
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