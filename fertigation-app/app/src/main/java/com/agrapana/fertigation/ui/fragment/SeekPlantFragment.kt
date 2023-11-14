package com.agrapana.fertigation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.FragmentSeekPlantBinding

class SeekPlantFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentSeekPlantBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeekPlantBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plantName = arguments?.getString("plantName")!!
//        val imgURL = arguments?.getString("imageURL")!!
        val status = arguments?.getString("status")!!
        val plantStarted = arguments?.getString("plantingStarted")!!
        val plantEnded = arguments?.getString("plantingEnded")!!

        binding.tvName.text = plantName
        if(status == "Done"){
            binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_50))
        } else {
            binding.tvStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_50))
        }
        binding.tvStatus.text = status.toUpperCase()
        binding.tvPlantingStarted.text = plantStarted
        binding.tvPlantingEnded.text = plantEnded
    }
}