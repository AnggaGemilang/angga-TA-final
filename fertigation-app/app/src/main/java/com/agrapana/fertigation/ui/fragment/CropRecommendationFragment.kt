package com.agrapana.fertigation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.agrapana.fertigation.databinding.FragmentSeekCropRecommendationBinding
import com.agrapana.fertigation.model.AIInput
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment

class CropRecommendationFragment(private var fieldId: String, private var cropNow: String) : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentSeekCropRecommendationBinding
    private var nValue: Int? = null
    private var pValue: Int? = null
    private var kValue: Int? = null
    private var temperatureValue: Int? = null
    private var moistureValue: Int? = null
    private var phValue: Int? = null
    private var rainfallValue: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeekCropRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}