package com.agrapana.arnesys.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrapana.arnesys.*
import com.agrapana.arnesys.adapter.FieldFilterAdapter
import com.agrapana.arnesys.config.MQTT_HOST
import com.agrapana.arnesys.databinding.FragmentHomeBinding
import com.agrapana.arnesys.helper.ChangeFieldListener
import com.agrapana.arnesys.helper.MqttClientHelper
import com.agrapana.arnesys.model.MonitoringAIProcessing
import com.agrapana.arnesys.model.MonitoringMainDevice
import com.agrapana.arnesys.model.MonitoringSupportDevice
import com.agrapana.arnesys.model.Suggestion
import com.agrapana.arnesys.ui.activity.LoginActivity
import com.agrapana.arnesys.ui.activity.SettingActivity
import com.agrapana.arnesys.viewmodel.FieldViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment: Fragment(), ChangeFieldListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var prefs: SharedPreferences
    private lateinit var recyclerViewAdapter: FieldFilterAdapter
    private lateinit var viewModel: FieldViewModel
    private lateinit var window: Window

    private var messageSupportDevice: MonitoringSupportDevice? = null
    private var pestListRisk: List<String>? = null
    private var clientId: String? = null
    private var fieldId: String? = null

    private val mqttClient by lazy {
        MqttClientHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        window = requireActivity().window
        prefs = this.activity?.getSharedPreferences("prefs",
            AppCompatActivity.MODE_PRIVATE
        )!!
        clientId = prefs.getString("client_id", "")

        val name: String? = prefs.getString("name", "")
        val nameParts = name!!.trim().split("\\s+".toRegex())
        binding.greeting.text = "Hello there, ${nameParts[0]}"

        binding.btnPest.setOnClickListener {
            if(binding.valPest.text != "N/A"){
                val dialog = SeekPestsFragment(pestListRisk)
                activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
            }
        }

        binding.toolbar.inflateMenu(R.menu.action_nav1)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.notification -> {
                    val suggestionList = ArrayList<Suggestion>()

                    if(messageSupportDevice != null){
                        if(messageSupportDevice?.monitoring?.soilNitrogen!! < 4){
                            suggestionList.add(Suggestion("Kekurangan Nitrogen", "Memberikan pupuk organic, seperti kotoran sapi, kotoran hewan, serbuk gergaji, atau menambahkan bakteri salah satunya azotobacter, Anabaena, dll."))
                        }

                        if(messageSupportDevice?.monitoring?.soilPhosphor!! < 4){
                            suggestionList.add(Suggestion("Kekurangan Phosphor", "Memberikan abu sekam padi 30%"))
                        }

                        if(messageSupportDevice?.monitoring?.soilKalium!! < 5){
                            suggestionList.add(Suggestion("Kekurangan Kalium", "Memberikan abu kayu, cangkang telur (disemprotkan pada daun), atau tepung tulang"))
                        }

                        if(messageSupportDevice?.monitoring?.soilPh!! < 5){
                            suggestionList.add(Suggestion("Kekurangan pH", "Memberikan abu kayu, atau dengan mikroorganisme seperti EM4"))
                        }

                        if(messageSupportDevice?.monitoring?.soilPh!! > 8){
                            suggestionList.add(Suggestion("Kelebihan pH", "Memberikan belerang, sulfur atau serbuk kayu"))
                        }

                        val dialog = SuggestionFragment(suggestionList)
                        activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                    }
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
                R.id.logout -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("You can't get in to your account")
                    builder.setPositiveButton("YES") { _, _ ->
                        val editor: SharedPreferences.Editor? = prefs.edit()
                        editor?.putBoolean("loginStart", true)
                        editor?.putString("client_id", null)
                        editor?.apply()
                        startActivity(Intent(activity, LoginActivity::class.java))
                    }
                    builder.setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }
            }
            true
        }

        val dtf = DateTimeFormatter.ofPattern("dd MMM")
        val localDate = LocalDate.now()
        binding.txtTanggalHome.text = dtf.format(localDate)

        binding.scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener {
                    _, _, scrollY, _, _ ->
                if(scrollY > 451){
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            })

        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.HORIZONTAL, false
        )
        binding.recyclerView.layoutManager = linearLayoutManager
        recyclerViewAdapter = FieldFilterAdapter(activity!!)
        recyclerViewAdapter.changeFieldListener = this
        binding.recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[FieldViewModel::class.java]
        viewModel.getAllField(clientId!!)
        viewModel.getLoadFieldObservable().observe(activity!!) {
            if(it?.data != null){
                binding.valFilterFieldPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                recyclerViewAdapter.setFieldList(it.data)
            }
        }
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connection to host connected:\n'$MQTT_HOST'")
                mqttClient.subscribe("arnesys/$fieldId/utama")
                mqttClient.subscribe("arnesys/$fieldId/utama/ai")
                mqttClient.subscribe("arnesys/$fieldId/pendukung/1")
            }
            override fun connectionLost(throwable: Throwable) {
                Log.w("Debug", "Connection to host lost:\n'$MQTT_HOST'")
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("mqttMessage", "Message received from host '$MQTT_HOST': $mqttMessage")
                when (topic) {
                    "arnesys/$fieldId/utama" -> {
                        val message = Gson().fromJson(mqttMessage.toString(), MonitoringMainDevice::class.java)
                        Log.d("/utama", message.toString())
                        binding.valWindTemperature.text = message.monitoring.windTemperature.toString() + "°"
                        binding.valWindHumidity.text = message.monitoring.windHumidity.toString() + "%"
                        binding.valWindSpeed.text = message.monitoring.windSpeed.toString() + " knot"
                        binding.valPressure.text = message.monitoring.windPressure.toString() + " hPa"
                        binding.valLight.text = message.monitoring.lightIntensity.toString() + " lux"
                    }
                    "arnesys/$fieldId/utama/ai" -> {
                        val message = Gson().fromJson(mqttMessage.toString(), MonitoringAIProcessing::class.java)
                        Log.d("/utama/ai", message.toString())

                        pestListRisk = message.aiProcessing.pestsPrediction!!.split(",")

                        var status = "Safe"
                        for (item in pestListRisk!!){
                            val data = item.split("=")
                            if(data[1] == "tinggi"){
                                status = "Risky"
                            }
                        }
                        binding.valPest.text = status

                        when (message.aiProcessing.weatherForecast) {
                            "Cerah-Berawan" -> {
                                Glide.with(requireContext()).load(R.drawable.sun_cloud).into(binding.valWeather)
                            }
                            "Hujan Lebat" -> {
                                Glide.with(requireContext()).load(R.drawable.rain).into(binding.valWeather)
                            }
                            else -> {
                                Glide.with(requireContext()).load(R.drawable.light_rain).into(binding.valWeather)
                            }
                        }

                        showAdditionalField()
                    }
                    "arnesys/$fieldId/pendukung/1" -> {
                        messageSupportDevice = Gson().fromJson(mqttMessage.toString(), MonitoringSupportDevice::class.java)
                        Log.d("/pendukung", messageSupportDevice.toString())
                        binding.valSoilTemperature.text = messageSupportDevice?.monitoring?.soilTemperature.toString() + "°"
                        binding.valSoilMoisture.text = messageSupportDevice?.monitoring?.soilHumidity.toString() + "%"
                        binding.valSoilPh.text = messageSupportDevice?.monitoring?.soilPh.toString()
                        binding.valSoilNitrogen.text = messageSupportDevice?.monitoring?.soilNitrogen.toString() + " mg/kg"
                        binding.valSoilPhosphor.text = messageSupportDevice?.monitoring?.soilPhosphor.toString() + " mg/kg"
                        binding.valSoilKalium.text = messageSupportDevice?.monitoring?.soilKalium.toString() + " mg/kg"
                    }
                }
                showMainField()
            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
    }

    override fun onChangeField(id: String?) {
        if (mqttClient.isConnected()) {
            hideMainField()
            mqttClient.unsubscribe("arnesys/$fieldId/utama")
            mqttClient.unsubscribe("arnesys/$fieldId/utama/ai")
            mqttClient.unsubscribe("arnesys/$fieldId/pendukung/1")
            hideAdditionalField()
        }
        fieldId = id
        mqttClient.subscribe("arnesys/$fieldId/utama")
        mqttClient.subscribe("arnesys/$fieldId/utama/ai")
        mqttClient.subscribe("arnesys/$fieldId/pendukung/1")
        setMqttCallBack()
    }

    private fun hideAdditionalField(){
        binding.valPest.visibility = View.GONE
        binding.valPestPlaceholder.visibility = View.VISIBLE
        binding.valWeather.visibility = View.GONE
        binding.valWeatherPlaceholder.visibility = View.VISIBLE
    }

    private fun showAdditionalField(){
        binding.valPestPlaceholder.visibility = View.GONE
        binding.valPest.visibility = View.VISIBLE
        binding.valWeatherPlaceholder.visibility = View.GONE
        binding.valWeather.visibility = View.VISIBLE
    }

    private fun hideMainField(){

        // Monitoring Atas

        binding.valWindTemperaturePlaceholder.visibility = View.VISIBLE
        binding.valWindHumidityPlaceholder.visibility = View.VISIBLE
        binding.valWindSpeedPlaceholder.visibility = View.VISIBLE
        binding.valPressurePlaceholder.visibility = View.VISIBLE
        binding.valLightPlaceholder.visibility = View.VISIBLE
        binding.valWindTemperature.visibility = View.GONE
        binding.valWindHumidity.visibility = View.GONE
        binding.valWindSpeed.visibility = View.GONE
        binding.valPressure.visibility = View.GONE
        binding.valLight.visibility = View.GONE

        // Monitoring Bawah

        binding.valTemperaturePlaceholder.visibility = View.VISIBLE
        binding.valSoilMoisturePlaceholder.visibility = View.VISIBLE
        binding.valSoilPhPlaceholder.visibility = View.VISIBLE
        binding.valSoilNitrogenPlaceholder.visibility = View.VISIBLE
        binding.valSoilPhosphorPlaceholder.visibility = View.VISIBLE
        binding.valSoilKaliumPlaceholder.visibility = View.VISIBLE
        binding.valSoilTemperature.visibility = View.GONE
        binding.valSoilMoisture.visibility = View.GONE
        binding.valSoilPh.visibility = View.GONE
        binding.valSoilNitrogen.visibility = View.GONE
        binding.valSoilPhosphor.visibility = View.GONE
        binding.valSoilKalium.visibility = View.GONE
    }

    private fun showMainField(){

        // Monitoring Atas

        binding.valWindTemperaturePlaceholder.visibility = View.GONE
        binding.valWindHumidityPlaceholder.visibility = View.GONE
        binding.valWindSpeedPlaceholder.visibility = View.GONE
        binding.valPressurePlaceholder.visibility = View.GONE
        binding.valLightPlaceholder.visibility = View.GONE
        binding.valWindTemperature.visibility = View.VISIBLE
        binding.valWindHumidity.visibility = View.VISIBLE
        binding.valWindSpeed.visibility = View.VISIBLE
        binding.valPressure.visibility = View.VISIBLE
        binding.valLight.visibility = View.VISIBLE

        // Monitoring Bawah

        binding.valTemperaturePlaceholder.visibility = View.GONE
        binding.valSoilMoisturePlaceholder.visibility = View.GONE
        binding.valSoilPhPlaceholder.visibility = View.GONE
        binding.valSoilNitrogenPlaceholder.visibility = View.GONE
        binding.valSoilPhosphorPlaceholder.visibility = View.GONE
        binding.valSoilKaliumPlaceholder.visibility = View.GONE
        binding.valSoilTemperature.visibility = View.VISIBLE
        binding.valSoilMoisture.visibility = View.VISIBLE
        binding.valSoilPh.visibility = View.VISIBLE
        binding.valSoilNitrogen.visibility = View.VISIBLE
        binding.valSoilPhosphor.visibility = View.VISIBLE
        binding.valSoilKalium.visibility = View.VISIBLE
    }

    override fun onResume() {
        mqttClient.mqttAndroidClient.registerResources()
        super.onResume()
    }
}