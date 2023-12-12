package com.agrapana.fertigation.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.FragmentAddPresetBinding
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.ParameterPreset
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*


class AddPresetFragment : RoundedBottomSheetDialogFragment(), OperationListener {

    private lateinit var viewModel: PresetViewModel
    private lateinit var binding: FragmentAddPresetBinding
    private var progressDialog: ProgressDialog? = null
    private var linkImage: Uri? = null
    private val GALLERY_REQUEST_CODE = 999
    private var layoutList: LinearLayout? = null

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

            val irrigationTimes = (arguments?.getString("irrigation_times"))!!.split(",")
            irrigationTimes.forEachIndexed { index, _ ->
                if(index == 0){
                    binding.irrigationTimes.text = Editable.Factory.getInstance().newEditable(irrigationTimes[index])
                } else {
                    addView("iTimes", irrigationTimes[index], "")
                }
            }

            val fertigationTimes = (arguments?.getString("fertigation_times"))!!.split(",")
            irrigationTimes.forEachIndexed { index, _ ->
                if(index == 0){
                    binding.fertigationTimes.text = Editable.Factory.getInstance().newEditable(fertigationTimes[index])
                } else {
                    addView("fTimes", irrigationTimes[index], "")
                }
            }

            val irrigationAges = (arguments?.getString("irrigation_ages"))!!.split(",")
            val irrigationDoses = (arguments?.getString("irrigation_doses"))!!.split(",")
            irrigationAges.forEachIndexed { index, _ ->
                if(index == 0){
                    binding.plantAgeIrrigation.text = Editable.Factory.getInstance().newEditable(irrigationAges[index])
                    binding.irrigationDose.text = Editable.Factory.getInstance().newEditable(irrigationDoses[index])
                } else {
                    addView("iDoses", irrigationAges[index], irrigationDoses[index])
                }
            }

            val fertigationAges = (arguments?.getString("fertigation_ages"))!!.split(",")
            val fertigationDoses = (arguments?.getString("fertigation_doses"))!!.split(",")
            fertigationAges.forEachIndexed { index, _ ->
                if(index == 0){
                    binding.plantAgeFertigation.text = Editable.Factory.getInstance().newEditable(fertigationAges[index])
                    binding.fertigationDose.text = Editable.Factory.getInstance().newEditable(fertigationDoses[index])
                } else {
                    addView("fDoses", fertigationAges[index], fertigationDoses[index])
                }
            }

            binding.irrigationDays.text = Editable.Factory.getInstance().newEditable(arguments?.getString("irrigation_days"))
        }

        binding.addItimesInput.setOnClickListener {
            addView("iTimes", "", "")
        }
        binding.addFtimesInput.setOnClickListener {
            addView("fTimes", "", "")
        }
        binding.addIdoseInput.setOnClickListener {
            addView("iDoses", "", "")
        }
        binding.addFdoseInput.setOnClickListener {
            addView("fDoses", "", "")
        }
        binding.open.setOnClickListener {
            selectImageFromGallery()
        }
        binding.irrigationTimePicker.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.irrigationTimes.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
        }
        binding.fertigationTimePicker.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                binding.fertigationTimes.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }
            TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
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

            val presetName = binding.presetName.text.toString().trim()
            val idealMoisture = binding.idealMoisture.text.toString().trim()
            val irrigationDays = binding.irrigationDays.text.toString().trim()

            val irrigationTimesList: LinearLayout = view.findViewById(R.id.irrigation_times_list) as LinearLayout
            var irrigationTimesVal: String = binding.irrigationTimes.text.toString().trim()
            irrigationTimesList.forEachIndexed { index, _ ->
                val editText = irrigationTimesList.getChildAt(index).findViewById(R.id.irrigation_times) as EditText
                irrigationTimesVal += "," + editText.text.toString().trim()
            }

            val fertigationDays = binding.fertigationDays.text.toString().trim()
            val fertigationTimesList: LinearLayout = view.findViewById(R.id.fertigation_times_list) as LinearLayout
            var fertigationTimesVal: String = binding.fertigationTimes.text.toString().trim()
            fertigationTimesList.forEachIndexed { index, _ ->
                val editText = fertigationTimesList.getChildAt(index).findViewById(R.id.fertigation_times) as EditText
                fertigationTimesVal += "," + editText.text.toString().trim()
            }

            val irrigationAgeList: LinearLayout = view.findViewById(R.id.irrigation_doses_list) as LinearLayout
            var irrigationAgeVal: String = binding.plantAgeIrrigation.text.toString().trim()
            var irrigationDoseVal: String = binding.irrigationDose.text.toString().trim()
            irrigationAgeList.forEachIndexed { index, _ ->
                val editTextAge = irrigationAgeList.getChildAt(index).findViewById(R.id.plant_age_irrigation) as EditText
                val editTextDose = irrigationAgeList.getChildAt(index).findViewById(R.id.irrigation_dose) as EditText
                irrigationAgeVal += "," + editTextAge.text.toString().trim()
                irrigationDoseVal += "," + editTextDose.text.toString().trim()
            }

            val fertigationAgeList: LinearLayout = view.findViewById(R.id.fertigation_doses_list) as LinearLayout
            var fertigationAgeVal: String = binding.plantAgeFertigation.text.toString().trim()
            var fertigationDoseVal: String = binding.fertigationDose.text.toString().trim()
            irrigationAgeList.forEachIndexed { index, _ ->
                val editTextAge = fertigationAgeList.getChildAt(index).findViewById(R.id.plant_age_fertigation) as EditText
                val editTextDose = irrigationAgeList.getChildAt(index).findViewById(R.id.irrigation_dose) as EditText
                fertigationAgeVal += "," + editTextAge.text.toString().trim()
                fertigationDoseVal += "," + editTextDose.text.toString().trim()
            }

            val parameterPreset = ParameterPreset()
            parameterPreset.presetName = presetName
            parameterPreset.idealMoisture = idealMoisture
            parameterPreset.irrigationDays = irrigationDays
            parameterPreset.irrigationTimes = irrigationTimesVal
            parameterPreset.fertigationDays = fertigationDays
            parameterPreset.fertigationTimes = fertigationTimesVal
            parameterPreset.irrigationAge = irrigationAgeVal
            parameterPreset.fertigationAge = fertigationAgeVal
            parameterPreset.irrigationDose = irrigationDoseVal
            parameterPreset.fertigationDose = fertigationDoseVal

            if(arguments?.getString("status") == "update") {
                parameterPreset.id = arguments?.getString("id")!!
                if(parameterPreset.presetName.isEmpty() || parameterPreset.fertigationDays.isEmpty() ||
                    parameterPreset.fertigationTimes.isEmpty() || parameterPreset.irrigationDays.isEmpty() ||
                    parameterPreset.irrigationTimes.isEmpty() || parameterPreset.irrigationAge.isEmpty() ||
                    parameterPreset.fertigationAge.isEmpty() || parameterPreset.idealMoisture.isEmpty() ||
                    parameterPreset.irrigationDose.isEmpty() || parameterPreset.fertigationDose.isEmpty()) {
                    Toast.makeText(requireContext(), "Fill all blanks input", Toast.LENGTH_LONG).show()
                    progressDialog!!.dismiss()
                } else if(parameterPreset.fertigationDays.toInt() > 45 || parameterPreset.irrigationDays.toInt() > 45){
                    Toast.makeText(requireContext(), "Days interval cannot greater that 45", Toast.LENGTH_LONG).show()
                    progressDialog!!.dismiss()
                } else if(linkImage == null){
                    parameterPreset.imageUrl = arguments?.getString("imageURL")!!
                    viewModel.onUpdatePreset(userId, parameterPreset)
                    progressDialog!!.dismiss()
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
                                    parameterPreset.imageUrl = imageUrl
                                    Log.d("Warko 2", parameterPreset.toString())
                                    viewModel.onUpdatePreset(userId, parameterPreset)
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
                if(parameterPreset.presetName.isEmpty() || parameterPreset.fertigationDays.isEmpty() ||
                    parameterPreset.fertigationTimes.isEmpty() || parameterPreset.irrigationDays.isEmpty() ||
                    parameterPreset.irrigationTimes.isEmpty() || parameterPreset.irrigationAge.isEmpty() ||
                    parameterPreset.fertigationAge.isEmpty() || parameterPreset.idealMoisture.isEmpty() ||
                    parameterPreset.irrigationDose.isEmpty() || parameterPreset.fertigationDose.isEmpty()) {
                    Toast.makeText(requireContext(), "Fill all blanks input", Toast.LENGTH_LONG).show()
                    progressDialog!!.dismiss()
                } else if(linkImage == null){
                    progressDialog!!.dismiss()
                    Toast.makeText(context, "Fill all blanks input", Toast.LENGTH_SHORT).show()
                    progressDialog!!.dismiss()
                } else if(parameterPreset.fertigationDays.toInt() > 45 || parameterPreset.irrigationDays.toInt() > 45){
                    Toast.makeText(requireContext(), "Days interval cannot greater that 45", Toast.LENGTH_LONG).show()
                    progressDialog!!.dismiss()
                } else {
                    val fileName = UUID.randomUUID().toString() +".png"
                    val refStorage = FirebaseStorage.getInstance().reference.child("thumbnail_preset/$fileName")
                    refStorage.putFile(linkImage!!)
                        .addOnSuccessListener { taskSnapshot ->
                            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                                val imageUrl = it.toString()
                                parameterPreset.imageUrl = imageUrl
                                viewModel.onAddPreset(userId, parameterPreset)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.d("gagal", e.message.toString())
                        }
                }
            }
        }
    }

    private fun addView(usedFor: String, firstData: String, secondData: String) {
        when (usedFor){
            "iTimes" -> {
                val templateView: View = layoutInflater.inflate(R.layout.template_irrigation_times_input, null, false)
                val imageClose = templateView.findViewById<View>(R.id.close_button) as ImageView
                val openTimePicker = templateView.findViewById<View>(R.id.irrigation_time_picker) as ImageView
                val irrigationTimeEd = templateView.findViewById<View>(R.id.irrigation_times) as EditText
                if(firstData.isNotEmpty()){
                    irrigationTimeEd.setText(firstData)
                }
                openTimePicker.setOnClickListener {
                    val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        irrigationTimeEd.setText(SimpleDateFormat("HH:mm").format(cal.time))
                    }
                    TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                }
                imageClose.setOnClickListener { removeView("iTimes", templateView) }
                binding.irrigationTimesList.addView(templateView)
            }
            "fTimes" -> {
                val templateView: View = layoutInflater.inflate(R.layout.template_fertigation_times_input, null, false)
                val imageClose = templateView.findViewById<View>(R.id.close_button) as ImageView
                val openTimePicker = templateView.findViewById<View>(R.id.fertigation_time_picker) as ImageView
                val fertigationTimesEd = templateView.findViewById<View>(R.id.fertigation_times) as EditText
                if(firstData.isNotEmpty()){
                    fertigationTimesEd.setText(firstData)
                }
                openTimePicker.setOnClickListener {
                    val cal = Calendar.getInstance()
                    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                        cal.set(Calendar.HOUR_OF_DAY, hour)
                        cal.set(Calendar.MINUTE, minute)
                        fertigationTimesEd.setText(SimpleDateFormat("HH:mm").format(cal.time))
                    }
                    TimePickerDialog(requireContext(), timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
                }
                imageClose.setOnClickListener { removeView("fTimes", templateView) }
                binding.fertigationTimesList.addView(templateView)
            }
            "iDoses" -> {
                val templateView: View = layoutInflater.inflate(R.layout.template_irrigation_dose_input, null, false)
                val imageClose = templateView.findViewById<View>(R.id.close_button) as ImageView
                imageClose.setOnClickListener { removeView("iDoses", templateView) }
                if(firstData.isNotEmpty()){
                    val irrigationDose = templateView.findViewById<View>(R.id.irrigation_dose) as EditText
                    irrigationDose.setText(firstData)
                }
                if(secondData.isNotEmpty()){
                    val irrigationAge = templateView.findViewById<View>(R.id.plant_age_irrigation) as EditText
                    irrigationAge.setText(secondData)
                }
                binding.irrigationDosesList.addView(templateView)
            }
            "fDoses" -> {
                val templateView: View = layoutInflater.inflate(R.layout.template_fertigation_dose_input, null, false)
                val imageClose = templateView.findViewById<View>(R.id.close_button) as ImageView
                imageClose.setOnClickListener { removeView("fDoses", templateView) }
                if(firstData.isNotEmpty()){
                    val fertigationDose = templateView.findViewById<View>(R.id.fertigation_dose) as EditText
                    fertigationDose.setText(firstData)
                }
                if(secondData.isNotEmpty()){
                    val fertigationAge = templateView.findViewById<View>(R.id.plant_age_fertigation) as EditText
                    fertigationAge.setText(secondData)
                }
                binding.fertigationDosesList.addView(templateView)
            }
        }
    }

    private fun removeView(usedFor: String, view: View) {
        when (usedFor){
            "iTimes" -> {
                binding.irrigationTimesList.removeView(view)
            }
            "fTimes" -> {
                binding.fertigationTimesList.removeView(view)
            }
            "iDoses" -> {
                binding.irrigationDosesList.removeView(view)
            }
            "fDoses" -> {
                binding.fertigationDosesList.removeView(view)
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