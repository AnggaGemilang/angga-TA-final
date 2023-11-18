package com.agrapana.fertigation.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.databinding.FragmentAddPresetBinding
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddPresetFragment : RoundedBottomSheetDialogFragment(), OperationListener {

    private lateinit var viewModel: PresetViewModel
    private lateinit var binding: FragmentAddPresetBinding
    private var progressDialog: ProgressDialog? = null
    private var linkImage: Uri? = null
    private val GALLERY_REQUEST_CODE = 999

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[PresetViewModel::class.java]
        viewModel.operationListener = this
        binding = FragmentAddPresetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(arguments?.getString("status") == "update"){
            binding.title.text = "Edit Preset"
            binding.btnSubmit.text = "Edit Preset"
            binding.presetName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("preset_name"))
            binding.idealMoisture.text = Editable.Factory.getInstance().newEditable(arguments?.getString("ideal_moisture"))
            binding.fertigationDays.text = Editable.Factory.getInstance().newEditable(arguments?.getString("fertigation_days"))
            binding.fertigationTimes.text = Editable.Factory.getInstance().newEditable(arguments?.getString("fertigation_times"))
            binding.irrigationDays.text = Editable.Factory.getInstance().newEditable(arguments?.getString("irrigation_days"))
            binding.irrigationTimes.text = Editable.Factory.getInstance().newEditable(arguments?.getString("irrigation_times"))
        }

        binding.open.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnSubmit.setOnClickListener {
            progressDialog = ProgressDialog(requireContext())
            progressDialog!!.setTitle("Please Wait")
            progressDialog!!.setMessage("System is working . . .")
            progressDialog!!.show()

            val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs",
                AppCompatActivity.MODE_PRIVATE
            )
            val userId: String = prefs.getString("client_id", "")!!

            if(arguments?.getString("status") == "update") {
                val presetName = binding.presetName.text.toString().trim()
                val idealMoisture = binding.idealMoisture.text.toString().trim()
                val irrigationDays = binding.irrigationDays.text.toString().trim()
                val irrigationTimes = binding.irrigationTimes.text.toString().trim()
                val fertigationDays = binding.fertigationDays.text.toString().trim()
                val fertigationTimes = binding.fertigationTimes.text.toString().trim()
                val preset = Preset()
                preset.id = arguments?.getString("id")!!
                preset.presetName = presetName
                preset.idealMoisture = idealMoisture
                preset.irrigationDays = irrigationDays
                preset.irrigationTimes = irrigationTimes
                preset.fertigationDays = fertigationDays
                preset.fertigationTimes = fertigationTimes
                if(linkImage == null){
                    preset.imageUrl = arguments?.getString("imageURL")!!
                    viewModel.onUpdatePreset(userId, preset)
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
                                    Log.d("Warko", "Sabihis bener kuduna kadieu")
                                    preset.imageUrl = imageUrl
                                    Log.d("Warko 2", preset.toString())
                                    viewModel.onUpdatePreset(userId, preset)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.d("gagal", e.message.toString())
                            }
                    }.addOnFailureListener {
                        Log.d("gagal", it.message.toString())
                    }
                }
            } else {
                if(linkImage == null){
                    progressDialog!!.dismiss()
                    Toast.makeText(context, "Fill all blanks input", Toast.LENGTH_SHORT).show()
                } else {
                    val fileName = UUID.randomUUID().toString() +".png"
                    val refStorage = FirebaseStorage.getInstance().reference.child("thumbnail_preset/$fileName")
                    refStorage.putFile(linkImage!!)
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                                val imageUrl = it.toString()
                                val presetName = binding.presetName.text.toString().trim()
                                val idealMoisture = binding.idealMoisture.text.toString().trim()
                                val fertigationDays = binding.fertigationDays.text.toString().trim()
                                val fertigationTimes = binding.fertigationTimes.text.toString().trim()
                                val irrigationDays = binding.irrigationDays.text.toString().trim()
                                val irrigationTimes = binding.irrigationTimes.text.toString().trim()
                                val preset = Preset()
                                preset.presetName = presetName
                                preset.idealMoisture = idealMoisture
                                preset.fertigationDays = fertigationDays
                                preset.fertigationTimes = fertigationTimes
                                preset.irrigationDays = irrigationDays
                                preset.irrigationTimes = irrigationTimes
                                preset.imageUrl = imageUrl
                                viewModel.onAddPreset(userId, preset)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.d("gagal", e.message.toString())
                        }
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

    override fun onSuccess() {
        progressDialog!!.dismiss()
        this.dismiss()
        if(arguments?.getString("status") == "update") {
            Toast.makeText(context, "Preset has updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Preset has added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(message: String) {
        progressDialog!!.dismiss()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}