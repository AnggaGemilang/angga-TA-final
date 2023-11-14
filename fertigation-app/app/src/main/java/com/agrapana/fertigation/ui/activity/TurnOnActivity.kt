package com.agrapana.fertigation.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.agrapana.fertigation.config.MQTT_HOST
import com.agrapana.fertigation.databinding.ActivityTurnOnBinding
import com.agrapana.fertigation.helper.MqttClientHelper
import com.agrapana.fertigation.helper.MqttClientHelper.Companion.TAG
import com.agrapana.fertigation.model.Common
import com.google.gson.Gson
import com.ncorti.slidetoact.SlideToActView
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.imaginativeworld.oopsnointernet.NoInternetDialog

class TurnOnActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTurnOnBinding
    private var commonMsg = Common()
    private var noInternetDialog: NoInternetDialog? = null

    private val mqttClient by lazy {
        MqttClientHelper(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTurnOnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding.slider.onSlideCompleteListener = object : SlideToActView.OnSlideCompleteListener{
            override fun onSlideComplete(view: SlideToActView) {
                Log.d(TAG, commonMsg.toString())
                binding.loadingPanel.visibility = View.VISIBLE
                setMqttCallBack()
            }
        }
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connection to host connected:\n'$MQTT_HOST'")
                mqttClient.subscribe("arceniter/common")
            }
            override fun connectionLost(throwable: Throwable) {
                Log.w("Debug", "Connection to host lost:\n'$MQTT_HOST'")
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w(TAG, "Message received from host '$MQTT_HOST': $mqttMessage")
                if(topic == "arceniter/common"){
                    commonMsg = Gson().fromJson(mqttMessage.toString(), Common::class.java)
                    commonMsg.power = "on"
                    mqttClient.publish("arceniter/common", Gson().toJson(commonMsg))
                    mqttClient.destroy()
                    startActivity(Intent(this@TurnOnActivity, MainActivity::class.java))
                }
            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
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