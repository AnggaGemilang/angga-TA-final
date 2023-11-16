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
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.FragmentPresetBinding

class PresetFragment : Fragment() {

    private lateinit var binding: FragmentPresetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPresetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.action_nav2)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.add -> {
                    val dialog = AddPresetFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "tambah")
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
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
            }
            true
        }
    }
}