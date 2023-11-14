package com.agrapana.fertigation.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.PlantsAdapter
import com.agrapana.fertigation.databinding.FragmentPlantListDataBinding
import com.agrapana.fertigation.model.Plant
import com.agrapana.fertigation.viewmodel.PlantViewModel

class PlantListDataFragment(private val type: String) : Fragment(), PlantsAdapter.TaskListener {

    private lateinit var viewModel: PlantViewModel
    private val adapter = PlantsAdapter(this)
    private lateinit var binding: FragmentPlantListDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlantListDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        viewModel.fetchPlants(type)
        viewModel.getRealtimeUpdates(type)
        binding.recyclerView.adapter = adapter
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this)[PlantViewModel::class.java]
        viewModel.plants.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.mainContent.visibility = View.VISIBLE
                binding.size.text = "Showing " + it.size.toString() + " data"
                adapter.setPlants(it)
            } else {
                binding.mainContent.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                binding.notFound.visibility = View.VISIBLE
            }
        }
    }

    override fun onDetailClick(view: View, plant: Plant) {
        val dialog = SeekPlantFragment()
        val bundle = Bundle()

        bundle.putString("plantName", plant.plantType.split("#").toTypedArray()[0])
        bundle.putString("status", plant.status)
        bundle.putString("plantingStarted", plant.plantStarted)
        bundle.putString("plantingEnded", plant.plantEnded)
        bundle.putString("imgURL", plant.imgUrl)
        dialog.arguments = bundle
        activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
    }

    override fun onOptionClick(view: View, plant: Plant) {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.MyPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.item_popup_action2, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.act_delete -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be deleted permanently")
                    builder.setPositiveButton("YES") { _, _ ->
                        viewModel.deletePlant(plant)
                        initViewModel()
                        viewModel.fetchPlants(type)
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