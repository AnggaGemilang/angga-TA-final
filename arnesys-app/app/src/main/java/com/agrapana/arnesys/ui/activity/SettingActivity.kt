package com.agrapana.arnesys.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.agrapana.arnesys.R
import com.agrapana.arnesys.config.MQTT_HOST
import com.agrapana.arnesys.databinding.ActivitySettingBinding
import com.agrapana.arnesys.helper.MqttClientHelper
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.imaginativeworld.oopsnointernet.NoInternetDialog


class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private var noInternetDialog: NoInternetDialog? = null

    private val mqttClient by lazy {
        MqttClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = ""

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setMqttCallBack()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connection to host connected:\n'$MQTT_HOST'")
                binding.loadingPanel.visibility = View.GONE
                binding.mainContent.visibility = View.VISIBLE
            }
            override fun connectionLost(throwable: Throwable) {
                Log.w("Debug", "Connection to host lost:\n'$MQTT_HOST'")
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {

            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val mqttClient by lazy {
            MqttClientHelper(requireContext())
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val refreshTime: Preference = findPreference("refresh")!!
            val notification: Preference = findPreference("notification")!!

            setMqttCallBack()

            refreshTime.setOnPreferenceChangeListener { _, newValue ->
                val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs", MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = prefs.edit()
                editor?.putString("refreshData", newValue.toString())
                editor?.apply()
                true
            }

            notification.setOnPreferenceChangeListener { _, newValue ->
                val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs", MODE_PRIVATE)
                val editor: SharedPreferences.Editor? = prefs.edit()
                editor?.putString("notificationAlert", newValue.toString())
                editor?.apply()
                true
            }
        }

        private fun setMqttCallBack() {
            mqttClient.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(b: Boolean, s: String) {
                    Log.w("Debug", "Connection to host connected:\n'$MQTT_HOST'")
                    mqttClient.subscribe("arceniter/thumbnail")
                }
                override fun connectionLost(throwable: Throwable) {
                    Log.w("Debug", "Connection to host lost:\n'$MQTT_HOST'")
                }
                @Throws(Exception::class)
                override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                    if(topic == "arceniter/thumbnail"){

                    }
                }
                override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                    Log.w("Debug", "Message published to host '$MQTT_HOST'")
                }
            })
        }

    }
}