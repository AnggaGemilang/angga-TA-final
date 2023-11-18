package com.agrapana.fertigation.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.PresetAdapter
import com.agrapana.fertigation.adapter.WorkerAdapter
import com.agrapana.fertigation.databinding.FragmentPresetBinding
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.model.User
import com.agrapana.fertigation.viewmodel.PresetViewModel

class PresetFragment : Fragment(), PresetAdapter.TaskListener {

    private lateinit var binding: FragmentPresetBinding
    private lateinit var viewModel: PresetViewModel
    private val adapter = PresetAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPresetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this)[PresetViewModel::class.java]
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
        initViewModel()
        binding.recyclerView.adapter = adapter
    }

    private fun initViewModel() {
        val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
        val clientId: String = prefs.getString("client_id", "")!!
        viewModel.fetchPresets(clientId)
        viewModel.getRealtimeUpdates(clientId)
        viewModel.preset.observe(viewLifecycleOwner) {
            adapter.addPreset(it)
            binding.notFound.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        viewModel.deletedPreset.observe(viewLifecycleOwner) {
            adapter.deletePreset(it)
        }
        viewModel.presets.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
                Log.d("dadang2", it.toString())
                binding.presetListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.presetListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.notFound.visibility = View.VISIBLE
            }
            adapter.setPreset(it)
        }
    }

    override fun onOptionClick(view: View, preset: Preset) {
        val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
        val clientId: String = prefs.getString("client_id", "")!!
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.MyPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.item_popup_action, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.act_edit -> {
                    val dialog = AddPresetFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "update")
                    bundle.putString("id", preset.id)
                    bundle.putString("preset_name", preset.presetName)
                    bundle.putString("imageURL", preset.imageUrl)
                    bundle.putString("ideal_moisture", preset.idealMoisture)
                    bundle.putString("fertigation_days", preset.fertigationDays)
                    bundle.putString("fertigation_times", preset.fertigationTimes)
                    bundle.putString("irrigation_days", preset.irrigationDays)
                    bundle.putString("irrigation_times", preset.irrigationTimes)
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                }
                R.id.act_delete -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be deleted permamently")
                    builder.setPositiveButton("YES") { _, _ ->
                        viewModel.onDeletePreset(clientId, preset)
                    }
                    builder.setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }
            }
            false
        }
    }
}