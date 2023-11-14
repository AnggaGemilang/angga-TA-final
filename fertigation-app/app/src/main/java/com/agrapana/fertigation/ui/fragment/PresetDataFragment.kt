package com.agrapana.fertigation.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.PresetsAdapter
import com.agrapana.fertigation.databinding.FragmentPresetDataBinding
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.viewmodel.PresetViewModel

class PresetDataFragment(private val type: String) : Fragment(), PresetsAdapter.TaskListener {

    private lateinit var binding: FragmentPresetDataBinding
    private lateinit var viewModel: PresetViewModel
    private val adapter = PresetsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[PresetViewModel::class.java]
        binding = FragmentPresetDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.fetchPresets(type)
        viewModel.getRealtimeUpdates(type)
        initViewModel()
        binding.recyclerView.adapter = adapter
    }

    private fun initViewModel() {
        var i = 0
        viewModel.preset.observe(viewLifecycleOwner) {
            if(!it.isDeleted){
                i++
            }
            binding.size.text = "Showing $i data"
            adapter.addPreset(it)
        }
        viewModel.presets.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.mainContent.visibility = View.VISIBLE
                i = it.size
                binding.size.text = "Showing " + it.size.toString() + " data"
                adapter.setPresets(it)
            } else {
                binding.mainContent.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.notFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onOptionClick(view: View, preset: Preset) {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.MyPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.item_popup_action, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.act_edit -> {
                    val dialog = AddConfigurationFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "update")
                    bundle.putString("id", preset.id)
                    bundle.putString("plantName", preset.plantName)
                    bundle.putString("imageURL", preset.imageUrl)
                    bundle.putString("category", preset.category)
                    bundle.putString("imageUrl", preset.imageUrl)
                    bundle.putString("nutrition", preset.nutrition)
                    bundle.putString("growthLamp", preset.growthLamp)
                    bundle.putString("gasValve", preset.gasValve)
                    bundle.putString("temperature", preset.temperature)
                    bundle.putString("pump", preset.pump)
                    bundle.putString("seedlingTime", preset.seedlingTime)
                    bundle.putString("growTime", preset.growTime)
                    bundle.putString("ph", preset.ph)
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                }
                R.id.act_delete -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be deleted permamently")
                    builder.setPositiveButton("YES") { _, _ ->
                        viewModel.deletePreset(preset)
                        initViewModel()
                        viewModel.fetchPresets(type)
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