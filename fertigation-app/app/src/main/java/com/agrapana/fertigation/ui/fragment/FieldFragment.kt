package com.agrapana.fertigation.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.FieldAdapter
import com.agrapana.fertigation.databinding.FragmentFieldBinding
import com.agrapana.fertigation.ui.activity.LoginActivity
import com.agrapana.fertigation.ui.activity.SettingActivity
import com.agrapana.fertigation.viewmodel.FieldViewModel
import com.agrapana.fertigation.viewmodel.PresetViewModel

class FieldFragment : Fragment() {

    private lateinit var binding: FragmentFieldBinding
    private lateinit var adapter: FieldAdapter
    private lateinit var viewModel: FieldViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFieldBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this)[FieldViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.inflateMenu(R.menu.action_nav2)
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.add -> {
                    val dialog = AddFieldFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "tambah")
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                }
                R.id.about -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("App Version")
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
        adapter = FieldAdapter(activity!!)
        viewModel.fetchPresets(clientId)
        viewModel.getRealtimeUpdates(clientId)
        viewModel.field.observe(viewLifecycleOwner) {
            adapter.addField(it)
            binding.notFound.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        viewModel.deletedField.observe(viewLifecycleOwner) {
            adapter.deleteField(it)
        }
        viewModel.fields.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
                binding.valFieldListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.valFieldListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.notFound.visibility = View.VISIBLE
            }
            adapter.setFieldList(it)
        }
    }

}