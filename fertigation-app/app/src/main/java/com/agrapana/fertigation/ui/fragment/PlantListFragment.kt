package com.agrapana.fertigation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.config.MQTT_HOST
import com.agrapana.fertigation.databinding.FragmentPlantListBinding
import com.agrapana.fertigation.helper.MqttClientHelper
import com.agrapana.fertigation.helper.MqttClientHelper.Companion.TAG
import com.agrapana.fertigation.model.Common
import com.agrapana.fertigation.ui.activity.SettingActivity
import com.agrapana.fertigation.ui.activity.TurnOnActivity
import com.agrapana.fertigation.viewmodel.PlantViewModel
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class PlantListFragment : Fragment() {

    private lateinit var binding: FragmentPlantListBinding
    private lateinit var viewModelPlant: PlantViewModel
    private lateinit var viewModelPreset: PresetViewModel

    private var commonMsg = Common()

    private val mqttClient by lazy {
        MqttClientHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMqttCallBack()
        initViewModel()
        viewModelPlant.getAllDataPlants()
        viewModelPreset.getAllDataPreset()

        binding.toolbar.inflateMenu(R.menu.action_nav1)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.power -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be perform the machine")
                    builder.setPositiveButton("YES") { _, _ ->
                        Log.d(TAG, commonMsg.toString())
                        binding.loadingPanel.visibility = View.VISIBLE
                        setMqttCallBackPower()
                    }
                    builder.setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }
                R.id.about -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Versi Aplikasi")
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

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Fruit"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Microgreen"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Ornamental"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Vegetable"))
        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        replaceFragment(PlantListDataFragment("Fruit"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> replaceFragment(PlantListDataFragment("Fruit"))
                    1 -> replaceFragment(PlantListDataFragment("Microgreen"))
                    2 -> replaceFragment(PlantListDataFragment("Ornamental"))
                    3 -> replaceFragment(PlantListDataFragment("Vegetable"))
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun initViewModel() {
        viewModelPlant = ViewModelProviders.of(this)[PlantViewModel::class.java]
        viewModelPlant.plants.observe(viewLifecycleOwner) {
            Log.d("asyiapp", it!!.size.toString())
            binding.tvBeenPlanted.text = it.size.toString()
            if (it.isNotEmpty()) {
                var i = 0
                for(plant in it){
                    if(plant.status == "Done"){
                        i++
                    }
                }
                binding.tvPlantHarvested.text = i.toString()

                i = 0
                for(plant in it){
                    if(plant.status == "Cancelled"){
                        i++
                    }
                }
                binding.tvPlantCancelled.text = i.toString()
            }
        }

        viewModelPreset = ViewModelProviders.of(this)[PresetViewModel::class.java]
        viewModelPreset.presets.observe(viewLifecycleOwner) {
            binding.tvNumberPreset.text = it!!.size.toString()
        }
    }

    private fun setMqttCallBackPower() {
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
                if(topic == "arceniter/common"){
                    commonMsg = Gson().fromJson(mqttMessage.toString(), Common::class.java)
                    commonMsg.power = "off"
                    mqttClient.publish("arceniter/common", Gson().toJson(commonMsg))
                    mqttClient.destroy()
                    startActivity(Intent(context, TurnOnActivity::class.java))
                }
            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
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
                if(topic == "arceniter/common"){
                    commonMsg = Gson().fromJson(mqttMessage.toString(), Common::class.java)
                    binding.tvBeingPlated.text = commonMsg.is_planting.capitalize()
                }
            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
    }

    fun replaceFragment(fragment: Fragment?) {
        val fm = requireActivity().supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.frame_container, fragment!!)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }
}