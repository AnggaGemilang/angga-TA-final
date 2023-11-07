package com.agrapana.irosysco.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.agrapana.irosysco.*
import com.agrapana.irosysco.config.MQTT_HOST
import com.agrapana.irosysco.databinding.FragmentHomeBinding
import com.agrapana.irosysco.helper.MqttClientHelper
import com.agrapana.irosysco.model.*
import com.agrapana.irosysco.ui.activity.DetailActivity
import com.agrapana.irosysco.ui.activity.SettingActivity
import com.agrapana.irosysco.ui.activity.TurnOnActivity
import com.agrapana.irosysco.viewmodel.PlantViewModel
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: PlantViewModel
    private var commonMsg = Common()
    private var controllingMsg = Controlling()
    private var thumbnailMsg = Thumbnail()
    private var monitoringMsg = Monitoring()

    private val mqttClient by lazy {
        MqttClientHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[PlantViewModel::class.java]
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.action_nav3)
        binding.toolbar.menu.getItem(1).isVisible = false
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.power -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be perform the machine")
                    builder.setPositiveButton("YES") { _, _ ->
                        binding.loadingPanel.visibility = View.VISIBLE
                        commonMsg.power = "off"
                        mqttClient.publish("arceniter/common", Gson().toJson(commonMsg))
                        startActivity(Intent(context, TurnOnActivity::class.java))
                    }
                    builder.setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }
                R.id.detail -> {
                    startActivity(Intent(requireContext(), DetailActivity::class.java))
                }
                R.id.about -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("App Version")
                        .setMessage("Beta 1.0.0")
                        .setCancelable(true)
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
                }
                R.id.setting -> {
                    startActivity(Intent(context, SettingActivity::class.java))
                }
            }
            true
        }
        val dtf = DateTimeFormatter.ofPattern("dd MMM")
        val localDate = LocalDate.now()
        binding.txtTanggalHome.text = dtf.format(localDate)
        setMqttCallBack()
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connection to host connected:\n'$MQTT_HOST'")
                mqttClient.subscribe("irosysco/common")
                mqttClient.subscribe("irosysco/thumbnail")
                mqttClient.subscribe("irosysco/monitoring")
            }
            override fun connectionLost(throwable: Throwable) {
                Log.w("Debug", "Connection to host lost:\n'$MQTT_HOST'")
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("Debug", "Message received from host '$MQTT_HOST': $mqttMessage")
                if(topic == "irosysco/common"){
                    commonMsg = Gson().fromJson(mqttMessage.toString(), Common::class.java)
                    val data = commonMsg.plant_name.split("#").toTypedArray()
                    binding.plantName.text = data[0].capitalize()
                    binding.startedPlanting.text = commonMsg.started_planting
                    if(commonMsg.is_planting == "no"){
                        binding.keteranganTidakAda.visibility = View.VISIBLE
                        binding.keteranganAda.visibility = View.GONE
                        binding.valTemperature.text = "N/A"
                        binding.valPh.text = "N/A"
                        binding.valGas.text = "N/A"
                        binding.valNutrition.text = "N/A"
                        binding.valNutritionVolume.text = "N/A"
                        binding.valGrowthLamp.text = "N/A"
                    } else {
                        binding.toolbar.menu.getItem(1).isVisible = true
                        binding.keteranganTidakAda.visibility = View.GONE
                        binding.keteranganAda.visibility = View.VISIBLE
                        binding.valTemperature.text = monitoringMsg.temperature.toString() + "°C"
                        binding.valPh.text = monitoringMsg.ph + " Ph"
                        binding.valGas.text = monitoringMsg.gas.toString() + " ppm`"
                        binding.valNutrition.text = monitoringMsg.nutrition.toString() + " ppm"
                        binding.valNutritionVolume.text = monitoringMsg.nutrition_volume.toString() + " %"
                        binding.valGrowthLamp.text = monitoringMsg.growth_lamp.capitalize()

                        val cal = Calendar.getInstance()
                        val s = SimpleDateFormat("dd-M-yyyy, hh:mm")
                        cal.add(Calendar.DAY_OF_YEAR, Integer.parseInt(data[1]))

                        if(s.format(Date(cal.timeInMillis)) == commonMsg.started_planting){
                            val progressDialog = ProgressDialog(requireContext())
                            progressDialog.setTitle("Please Wait")
                            progressDialog.setMessage("System is working . . .")
                            progressDialog.show()

                            val plant = Plant()
                            plant.imgUrl = thumbnailMsg.imgURL
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
                                    Toast.makeText(requireContext(), "Preset has done", Toast.LENGTH_SHORT).show()
                                }
                            }

                            commonMsg.is_planting = "no"
                            commonMsg.started_planting = ""
                            commonMsg.plant_name = ""
                            commonMsg.category = ""

                            Log.d("dadang dong", commonMsg.toString())

                            mqttClient.publish("irosysco/common", Gson().toJson(commonMsg))

                            val thumbnail = Thumbnail()
                            thumbnail.imgURL = ""
                            thumbnail.ref = thumbnailMsg.ref
                            mqttClient.publish("irosysco/thumbnail", Gson().toJson(thumbnail))
                        }
                    }
                } else if (topic == "irosysco/monitoring"){
                    monitoringMsg = Gson().fromJson(mqttMessage.toString(), Monitoring::class.java)
                } else if (topic == "irosysco/thumbnail"){
                    thumbnailMsg = Gson().fromJson(mqttMessage.toString(), Thumbnail::class.java)
                    Glide.with(this@HomeFragment)
                        .load(thumbnailMsg.imgURL)
                        .into(binding.image)
                }
                binding.plantNamePlaceholder.visibility = View.GONE
                binding.imagePlaceholder.visibility = View.GONE
                binding.startedPlantingPlaceholder.visibility = View.GONE
                binding.valTemperaturePlaceholder.visibility = View.GONE
                binding.valGasPlaceholder.visibility = View.GONE
                binding.valPhPlaceholder.visibility = View.GONE
                binding.valVolumePlaceholder.visibility = View.GONE
                binding.valNutritionPlaceholder.visibility = View.GONE
                binding.valGasPlaceholder.visibility = View.GONE
                binding.valGrowthLampPlaceholder.visibility = View.GONE
                binding.plantName.visibility = View.VISIBLE
                binding.image.visibility = View.VISIBLE
                binding.startedPlanting.visibility = View.VISIBLE
                binding.valTemperature.visibility = View.VISIBLE
                binding.valGas.visibility = View.VISIBLE
                binding.valPh.visibility = View.VISIBLE
                binding.valGas.visibility = View.VISIBLE
                binding.valGrowthLamp.visibility = View.VISIBLE
                binding.valNutritionVolume.visibility = View.VISIBLE
                binding.valNutrition.visibility = View.VISIBLE
            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
    }

    override fun onDestroy() {
//        if (mqttClient.isConnected()) {
//            mqttClient.destroy()
//        }
        super.onDestroy()
    }

}