package com.agrapana.arnesys.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.agrapana.arnesys.config.MQTT_HOST
import com.agrapana.arnesys.databinding.FragmentSeekCropRecommendationBinding
import com.agrapana.arnesys.helper.MqttClientHelper
import com.agrapana.arnesys.model.AIInput
import com.agrapana.arnesys.model.MonitoringAIProcessing
import com.agrapana.arnesys.model.MonitoringMainDevice
import com.agrapana.arnesys.model.MonitoringSupportDevice
import com.agrapana.arnesys.viewmodel.CropRecommendationViewModel
import com.agrapana.arnesys.viewmodel.PestPredictionViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.gson.Gson
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

class CropRecommendationFragment(private var fieldId: String, private var cropNow: String) : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentSeekCropRecommendationBinding
    private lateinit var viewModelCropRecommendation: CropRecommendationViewModel
    private var nValue: Int? = null
    private var pValue: Int? = null
    private var kValue: Int? = null
    private var temperatureValue: Int? = null
    private var moistureValue: Int? = null
    private var phValue: Int? = null
    private var rainfallValue: Int? = null

    private val mqttClient by lazy {
        MqttClientHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeekCropRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMqttCallBack()
    }

    private fun initViewModelAI(inputCropRecommendation: AIInput) {
        viewModelCropRecommendation = ViewModelProvider(this)[CropRecommendationViewModel::class.java]
        viewModelCropRecommendation.getCropRecommend(inputCropRecommendation)
        viewModelCropRecommendation.getLoadCropRecommendObservable().observe(activity!!) {
            binding.txtCrop.text = it!!.recommendCrop!!.capitalize()

            if(it.recommendCrop == cropNow) {
                binding.txtCropDetail.text = "Based on the dataset used, the results of AI processing indicate that this land is already suitable for ${it.recommendCrop}"
            } else {
                binding.txtCropDetail.text = "Based on the dataset used, the results of AI processing indicate that this land is suitable for ${it.recommendCrop} With the current land being used for $cropNow, further studies are needed regarding the suitability of this land for the type of crops to be planted"
            }
        }
    }

    private fun setMqttCallBack() {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(b: Boolean, s: String) {
                Log.w("Debug", "Connection to host connected:\n'$MQTT_HOST'")
                mqttClient.subscribe("arnesys/$fieldId/utama")
                mqttClient.subscribe("arnesys/$fieldId/pendukung/1")
            }
            override fun connectionLost(throwable: Throwable) {
                Log.w("Debug", "Connection to host lost:\n'$MQTT_HOST'")
            }
            @Throws(Exception::class)
            override fun messageArrived(topic: String, mqttMessage: MqttMessage) {
                Log.w("mqttMessage", "Message received from host '$MQTT_HOST': $mqttMessage")
                if(topic == "arnesys/$fieldId/utama"){
                    val message = Gson().fromJson(mqttMessage.toString(), MonitoringMainDevice::class.java)
                    Log.d("/utama", message.toString())
                    rainfallValue = message.monitoring.rainfall
                } else if(topic == "arnesys/$fieldId/pendukung/1"){
                    val message = Gson().fromJson(mqttMessage.toString(), MonitoringSupportDevice::class.java)
                    Log.d("/pendukung", message.toString())
                    nValue = message.monitoring.soilNitrogen
                    pValue = message.monitoring.soilPhosphor
                    kValue = message.monitoring.soilKalium
                    temperatureValue = message.monitoring.soilTemperature
                    moistureValue = message.monitoring.soilHumidity
                    phValue = message.monitoring.soilPh
                }

                if(nValue != null && pValue != null && kValue != null && temperatureValue != null && moistureValue != null && phValue != null && rainfallValue != null) {
                    mqttClient.unsubscribe("arnesys/$fieldId/utama")
                    mqttClient.unsubscribe("arnesys/$fieldId/pendukung/1")

                    binding.mainWrapper.visibility = View.VISIBLE
                    binding.loaderWrapper.visibility = View.GONE

                    val arrayList: ArrayList<Int> = ArrayList()
                    arrayList.add(nValue!!)
                    arrayList.add(pValue!!)
                    arrayList.add(kValue!!)
                    arrayList.add(temperatureValue!!)
                    arrayList.add(moistureValue!!)
                    arrayList.add(phValue!!)
                    arrayList.add(rainfallValue!!)

                    val data = AIInput(arrayList)

                    initViewModelAI(data)
                }

            }
            override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
                Log.w("Debug", "Message published to host '$MQTT_HOST'")
            }
        })
    }

}