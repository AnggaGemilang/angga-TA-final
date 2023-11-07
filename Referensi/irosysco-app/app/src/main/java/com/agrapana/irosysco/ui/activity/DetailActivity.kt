package com.agrapana.irosysco.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.agrapana.irosysco.R
import com.agrapana.irosysco.config.MQTT_HOST
import com.agrapana.irosysco.databinding.ActivityDetailBinding
import com.agrapana.irosysco.helper.MqttClientHelper
import com.agrapana.irosysco.model.*
import com.agrapana.irosysco.viewmodel.PlantViewModel
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.imaginativeworld.oopsnointernet.NoInternetDialog
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: PlantViewModel
    private var noInternetDialog: NoInternetDialog? = null
    private var commonMsg =  Common()
    private var thumbnailMsg =  Thumbnail()
    private var controllingMsg =  Controlling()
    private var imageFromMQTT: String = ""
    lateinit var lineList: ArrayList<Entry>
    lateinit var lineDataSet: LineDataSet
    lateinit var lineData: LineData

    private val mqttClient by lazy {
        MqttClientHelper(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        viewModel = ViewModelProviders.of(this)[PlantViewModel::class.java]

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = ""

        lineList = ArrayList()
        lineList.add(Entry(10f, 1f))
        lineList.add(Entry(12f, 2f))
        lineList.add(Entry(15f, 3f))
        lineList.add(Entry(20f, 4f))

        lineDataSet = LineDataSet(lineList, "Perkembangan Jumlah Daun")
        lineData = LineData(lineDataSet)
        binding.chart.data = lineData
        lineDataSet.setColors(*ColorTemplate.JOYFUL_COLORS)
        lineDataSet.valueTextColor = Color.BLACK
        lineDataSet.valueTextSize = 14f
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillDrawable = ContextCompat.getDrawable(this, R.drawable.gradient_chart)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decor = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        setMqttCallBack()
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connection to host connected:\n'$MQTT_HOST'")
                mqttClient.subscribe("arceniter/common")
                mqttClient.subscribe("arceniter/monitoring")
                mqttClient.subscribe("arceniter/controlling")
                mqttClient.subscribe("arceniter/thumbnail")
            }

            override fun connectionLost(throwable: Throwable) {
                Log.w("Debug", "Connection to host lost:\n'$MQTT_HOST'")
            }

            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                binding.mainContent.visibility = View.VISIBLE
                binding.loadingPanel.visibility = View.GONE
                Log.w("Debug", "Message received from host '$MQTT_HOST': $mqttMessage")
                when (topic) {
                    "arceniter/common" -> {
                        commonMsg = Gson().fromJson(mqttMessage.toString(), Common::class.java)
                        val data = commonMsg.plant_name.split("#").toTypedArray()
                        binding.plantName.text = data[0].capitalize()
                        binding.startedPlanting.text = commonMsg.started_planting

                        val cal = Calendar.getInstance()
                        val s = SimpleDateFormat("dd MMM yyyy")
                        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(data[1]))
                        binding.prediction.text = "(" + s.format(Date(cal.timeInMillis)) + ")"
                    }
                    "arceniter/monitoring" -> {
                        val monitoring = Gson().fromJson(mqttMessage.toString(), Monitoring::class.java)
                        binding.valTemperature.text = monitoring.temperature.toString() + "°C"
                        binding.valPh.text = monitoring.ph + " Ph"
                        binding.valGas.text = monitoring.gas.toString() + " ppm`"
                        binding.valNutrition.text = monitoring.nutrition.toString() + " ppm"
                        binding.valNutritionVolume.text = monitoring.nutrition_volume.toString() + "%"
                        binding.valGrowthLamp.text = monitoring.growth_lamp.capitalize()
                    }
                    "arceniter/controlling" -> {
                        val controlling = Gson().fromJson(mqttMessage.toString(), Controlling::class.java)
                        controllingMsg = controlling
                    }
                    "arceniter/thumbnail" -> {
                        thumbnailMsg = Gson().fromJson(mqttMessage.toString(), Thumbnail::class.java)
                        binding.image.visibility = View.VISIBLE
                        Glide.with(this@DetailActivity)
                            .load(thumbnailMsg.imgURL)
                            .into(binding.image)
                        imageFromMQTT = thumbnailMsg.imgURL
                    }
                }
            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.camera -> {
                startActivity(Intent(this, CameraWebviewActivity::class.java))
            }
            R.id.done -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Are You Sure?")
                builder.setMessage("This can be perform the machine")
                builder.setPositiveButton("YES") { _, _ ->
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Please Wait")
                    progressDialog.setMessage("System is working . . .")
                    progressDialog.show()

                    val plant = Plant()
                    plant.imgUrl = imageFromMQTT
                    plant.category = commonMsg.category
                    plant.plantType = commonMsg.plant_name
                    plant.mode = controllingMsg.mode
                    plant.plantStarted = commonMsg.started_planting
                    val sdf = SimpleDateFormat("dd-M-yyyy, hh:mm")
                    plant.plantEnded = sdf.format(Date())
                    plant.status = "Done"

                    val dbPlants = viewModel.getDBReference()
                    plant.id = dbPlants.push().key.toString()
                    dbPlants.child(plant.id).setValue(plant).addOnCompleteListener {
                        if(it.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Preset has done", Toast.LENGTH_SHORT).show()
                        }
                    }

                    commonMsg.is_planting = "no"
                    commonMsg.started_planting = ""
                    commonMsg.plant_name = ""
                    commonMsg.category = ""

                    Log.d("dadang dong", commonMsg.toString())

                    mqttClient.publish("arceniter/common", Gson().toJson(commonMsg))

                    val thumbnail = Thumbnail()
                    thumbnail.imgURL = ""
                    thumbnail.ref = thumbnailMsg.ref
                    mqttClient.publish("arceniter/thumbnail", Gson().toJson(thumbnail))
                    startActivity(Intent(this, MainActivity::class.java))
                }
                builder.setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }
            R.id.cancel -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Are You Sure?")
                builder.setMessage("This can be perform the machine")
                builder.setPositiveButton("YES") { _, _ ->
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Please Wait")
                    progressDialog.setMessage("System is working . . .")
                    progressDialog.show()

                    val plant = Plant()
                    plant.imgUrl = imageFromMQTT
                    plant.category = commonMsg.category
                    plant.plantType = commonMsg.plant_name
                    plant.mode = controllingMsg.mode
                    plant.plantStarted = commonMsg.started_planting
                    val sdf = SimpleDateFormat("dd-M-yyyy, hh:mm")
                    plant.plantEnded = sdf.format(Date())
                    plant.status = "Cancelled"

                    val dbPlants = viewModel.getDBReference()
                    plant.id = dbPlants.push().key.toString()
                    dbPlants.child(plant.id).setValue(plant).addOnCompleteListener {
                        if(it.isSuccessful) {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Preset has cancelled", Toast.LENGTH_SHORT).show()
                        }
                    }
                    commonMsg.is_planting = "no"
                    commonMsg.started_planting = ""
                    commonMsg.plant_name = ""
                    mqttClient.publish("arceniter/common", Gson().toJson(commonMsg))

                    val thumbnail = Thumbnail()
                    thumbnail.imgURL = ""
                    thumbnail.ref = thumbnailMsg.ref
                    mqttClient.publish("arceniter/thumbnail", Gson().toJson(thumbnail))

                    startActivity(Intent(this, MainActivity::class.java))
                }
                builder.setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
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