package com.agrapana.fertigation.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.PresetAdapter
import com.agrapana.fertigation.databinding.FragmentPresetBinding
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.ParameterPreset
import com.agrapana.fertigation.ui.activity.SettingActivity
import com.agrapana.fertigation.viewmodel.PresetViewModel

class PresetFragment : Fragment(), PresetAdapter.TaskListener, OperationListener {

    private lateinit var binding: FragmentPresetBinding
    private lateinit var viewModel: PresetViewModel
    private lateinit var prefs: SharedPreferences
    private val adapter = PresetAdapter(this)
    private lateinit var window: Window

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPresetBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this)[PresetViewModel::class.java]
        viewModel.operationListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = this.activity?.getSharedPreferences("prefs",
            AppCompatActivity.MODE_PRIVATE
        )!!
        binding.toolbar.inflateMenu(R.menu.action_nav2)
        val role: String? = prefs.getString("client_role", "")
        if(role == "Worker"){
            binding.toolbar.menu.findItem(R.id.add).isVisible = false
        }
        window = requireActivity().window
        binding.scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener {
                    _, _, scrollY, _, _ ->
                if(scrollY > 715){
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            }
        )
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.add -> {
                    val dialog = AddPresetFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "tambah")
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                }
                R.id.setting -> {
                    startActivity(Intent(context, SettingActivity::class.java))
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
        val clientId: String = prefs.getString("client_id", "")!!
        viewModel.fetchPresets(clientId)
        viewModel.getRealtimeUpdates(clientId)
        viewModel.parameterPreset.observe(viewLifecycleOwner) {
            adapter.addPreset(it)
            binding.notFound.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        viewModel.deletedParameterPreset.observe(viewLifecycleOwner) {
            adapter.deletePreset(it)
        }
        viewModel.presets.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
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

    override fun onOptionClick(view: View, parameterPreset: ParameterPreset) {
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
                    bundle.putString("id", parameterPreset.id)
                    bundle.putString("preset_name", parameterPreset.presetName)
                    bundle.putString("imageURL", parameterPreset.imageUrl)
                    bundle.putString("ideal_moisture", parameterPreset.idealMoisture)
                    bundle.putString("fertigation_days", parameterPreset.fertigationDays)
                    bundle.putString("fertigation_times", parameterPreset.fertigationTimes)
                    bundle.putString("irrigation_days", parameterPreset.irrigationDays)
                    bundle.putString("irrigation_times", parameterPreset.irrigationTimes)
                    bundle.putString("irrigation_ages", parameterPreset.irrigationAge)
                    bundle.putString("fertigation_ages", parameterPreset.fertigationAge)
                    bundle.putString("fertigation_doses", parameterPreset.fertigationDose)
                    bundle.putString("irrigation_doses", parameterPreset.irrigationDose)
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                }
                R.id.act_delete -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be deleted permamently")
                    builder.setPositiveButton("YES") { _, _ ->
                        viewModel.onDeletePreset(clientId, parameterPreset)
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

        binding.scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener {
                    _, _, scrollY, _, _ ->
                if(scrollY > 451){
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            })
    }

    override fun onSuccess() {
        Toast.makeText(requireContext(), "Preset is deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}