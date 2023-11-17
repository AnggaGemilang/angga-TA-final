package com.agrapana.fertigation.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.databinding.FragmentAddPresetBinding
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddWorkerFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var viewModel: PresetViewModel
    private lateinit var binding: FragmentAddPresetBinding
    private var linkImage: Uri? = null
    private val GALLERY_REQUEST_CODE = 999

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[PresetViewModel::class.java]
        binding = FragmentAddPresetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments?.getString("status") == "update"){
            binding.title.text = "Edit Configuration"
            binding.btnSubmit.text = "Edit Configuration"
            binding.plantName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("plantName"))
            binding.temperature.text = Editable.Factory.getInstance().newEditable(arguments?.getString("temperature"))
            binding.seedling.text = Editable.Factory.getInstance().newEditable(arguments?.getString("seedlingTime"))
            binding.grow.text = Editable.Factory.getInstance().newEditable(arguments?.getString("growTime"))
            binding.category.setSelection(
                (binding.category.adapter as ArrayAdapter<String>).getPosition(
                    arguments?.getString("category")
                )
            )
            binding.growthLamp.setSelection(
                (binding.growthLamp.adapter as ArrayAdapter<String>).getPosition(
                    arguments?.getString("growthLamp")
                )
            )
            binding.ph.text = Editable.Factory.getInstance().newEditable(arguments?.getString("ph"))
            binding.gasValve.text = Editable.Factory.getInstance().newEditable(arguments?.getString("gasValve"))
            binding.pump.setSelection(
                (binding.pump.adapter as ArrayAdapter<String>).getPosition(
                    arguments?.getString("pump")
                )
            )
            binding.nutrition.text = Editable.Factory.getInstance().newEditable(arguments?.getString("nutrition"))
        }

        binding.open.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnSubmit.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("System is working . . .")
            progressDialog.show()

            if(arguments?.getString("status") == "update") {
                val name = binding.plantName.text.toString().trim()
                val category = binding.category.selectedItem.toString()
                val nutrition = binding.nutrition.text.toString().trim()
                val growthLamp = binding.growthLamp.selectedItem.toString()
                val gasValve = binding.gasValve.text.toString().trim()
                val temperature = binding.temperature.text.toString().trim()
                val pump = binding.pump.selectedItem.toString()
                val ph = binding.ph.text.toString().trim()
                val seedlingTime = binding.seedling.text.toString().trim()
                val growTime = binding.grow.text.toString().trim()
                val preset = Preset()
                preset.id = arguments?.getString("id")!!
                preset.plantName = name
                preset.category = category
                preset.nutrition = nutrition
                preset.growthLamp = growthLamp
                preset.gasValve = gasValve
                preset.temperature = temperature
                preset.pump = pump
                preset.ph = ph
                preset.seedlingTime = seedlingTime
                preset.growTime = growTime
                preset.isDeleted = true
                if(linkImage == null){
                    preset.imageUrl = arguments?.getString("imageURL")!!
                } else {
                    val storageReference = FirebaseStorage.getInstance()
                        .getReferenceFromUrl(arguments?.getString("imageURL")!!)
                    storageReference.delete().addOnSuccessListener {
                        val fileName = UUID.randomUUID().toString() +".png"
                        val refStorage = FirebaseStorage.getInstance().reference.child("thumbnail_preset/$fileName")
                        refStorage.putFile(linkImage!!)
                            .addOnSuccessListener { taskSnapshot ->
                                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                                    val imageUrl = it.toString()
                                    preset.imageUrl = imageUrl
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.d("gagal", e.message.toString())
                            }
                    }.addOnFailureListener {
                        Log.d("gagal", it.message.toString())
                    }
                }
                val dbPresets = viewModel.getDBReference()
                dbPresets.child(preset.id).setValue(preset).addOnCompleteListener {
                    if(it.isSuccessful) {
                        progressDialog.dismiss()
                        this.dismiss()
                        Toast.makeText(context, "Preset has updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val fileName = UUID.randomUUID().toString() +".png"
                val refStorage = FirebaseStorage.getInstance().reference.child("thumbnail_preset/$fileName")
                refStorage.putFile(linkImage!!)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            val imageUrl = it.toString()
                            val name = binding.plantName.text.toString().trim()
                            val category = binding.category.selectedItem.toString()
                            val nutrition = binding.nutrition.text.toString().trim()
                            val growthLamp = binding.growthLamp.selectedItem.toString()
                            val gasValve = binding.gasValve.text.toString().trim()
                            val temperature = binding.temperature.text.toString().trim()
                            val pump = binding.pump.selectedItem.toString()
                            val seedlingTime = binding.seedling.text.toString().trim()
                            val growTime = binding.grow.text.toString().trim()
                            val ph = binding.ph.text.toString().trim()
                            val preset = Preset()
                            preset.plantName = name
                            preset.category = category
                            preset.nutrition = nutrition
                            preset.growthLamp = growthLamp
                            preset.gasValve = gasValve
                            preset.temperature = temperature
                            preset.pump = pump
                            preset.ph = ph
                            preset.seedlingTime = seedlingTime
                            preset.growTime = growTime
                            preset.imageUrl = imageUrl
                            preset.isDeleted = false
                            val dbPresets = viewModel.getDBReference()
                            preset.id = dbPresets.push().key.toString()
                            dbPresets.child(preset.id).setValue(preset).addOnCompleteListener { it1 ->
                                if(it1.isSuccessful) {
                                    progressDialog.dismiss()
                                    this.dismiss()
                                    Toast.makeText(context, "Preset has added successfully", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d("gagal", e.message.toString())
                    }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )
        if (requestCode == GALLERY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            val fileURL = data.data
            val urlFile = data.data!!.path.toString()
            binding.txtFilename.text = if(urlFile.length > 21) urlFile.substring(0, 20) + "..." else urlFile
            linkImage = fileURL!!
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Please select..."
            ),
            GALLERY_REQUEST_CODE
        )
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}