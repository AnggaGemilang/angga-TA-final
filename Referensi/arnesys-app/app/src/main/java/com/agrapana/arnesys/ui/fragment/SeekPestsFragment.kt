package com.agrapana.arnesys.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agrapana.arnesys.R
import com.agrapana.arnesys.databinding.FragmentHomeBinding
import com.agrapana.arnesys.databinding.FragmentSeekPestsBinding
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment

class SeekPestsFragment(private var pestPrediction: String?) : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentSeekPestsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeekPestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.txtThripidae.text = pestPrediction
    }

}