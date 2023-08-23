package com.agrapana.arnesys.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrapana.arnesys.R
import com.agrapana.arnesys.adapter.FieldAdapter
import com.agrapana.arnesys.adapter.SuggestionAdapter
import com.agrapana.arnesys.databinding.FragmentSeekPestsBinding
import com.agrapana.arnesys.databinding.FragmentSuggestionBinding
import com.agrapana.arnesys.model.Suggestion
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment

class SuggestionFragment(private val suggestionList: ArrayList<Suggestion>) : RoundedBottomSheetDialogFragment() {

    private lateinit var binding: FragmentSuggestionBinding
    private lateinit var recyclerViewAdapter: SuggestionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuggestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(suggestionList.isNotEmpty()){
            initRecyclerView()
        }
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(
            activity, LinearLayoutManager.VERTICAL, false
        )
        binding.recyclerView.layoutManager = linearLayoutManager
        recyclerViewAdapter = SuggestionAdapter(activity!!, suggestionList)
        binding.recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.notifyDataSetChanged()
    }

}