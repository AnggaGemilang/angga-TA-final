package com.agrapana.fertigation.ui.activity

import android.annotation.SuppressLint
import android.content.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.agrapana.fertigation.databinding.ActivitySplashScreenBinding
import org.imaginativeworld.oopsnointernet.NoInternetDialog

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private var binding: ActivitySplashScreenBinding? = null
    private var noInternetDialog: NoInternetDialog? = null

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        var progressStatus = 0
        binding!!.progressBar.max = 100

        Thread {
            while (progressStatus < binding!!.progressBar.max) {
                progressStatus++
                binding!!.progressBar.progress = progressStatus
                try {
                    Thread.sleep(15)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            Handler(Looper.getMainLooper()).post {
                val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
                val firstState: Boolean = prefs.getBoolean("first_start_status", true)
                val loginState: Boolean = prefs.getBoolean("login_status", true)

                if(firstState){
                    startActivity(Intent(this, OnboardingActivity::class.java))
                } else if (loginState) {
                    startActivity(Intent(this, LoginActivity::class.java))
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }.start()
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