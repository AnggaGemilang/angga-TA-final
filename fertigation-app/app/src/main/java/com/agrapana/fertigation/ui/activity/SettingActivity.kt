package com.agrapana.fertigation.ui.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProviders
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.ActivitySettingBinding
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.IntervalPreset
import com.agrapana.fertigation.viewmodel.PresetViewModel
import org.imaginativeworld.oopsnointernet.NoInternetDialog


class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private var noInternetDialog: NoInternetDialog? = null

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
        supportActionBar?.title = ""

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPause() {
        super.onPause()
        if (noInternetDialog != null) {
            noInternetDialog!!.destroy();
        }
    }

    override fun onResume() {
        super.onResume()
        val builder1 = NoInternetDialog.Builder(this)
        builder1.cancelable = false // Optional
        builder1.noInternetConnectionTitle = "No Internet" // Optional
        builder1.noInternetConnectionMessage = "Check your Internet connection and try again" // Optional
        builder1.showInternetOnButtons = true // Optional
        builder1.pleaseTurnOnText = "Please turn on" // Optional
        builder1.wifiOnButtonText = "Wifi" // Optional
        builder1.mobileDataOnButtonText = "Mobile data" // Optional
        builder1.onAirplaneModeTitle = "No Internet" // Optional
        builder1.onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
        builder1.pleaseTurnOffText = "Please turn off" // Optional
        builder1.airplaneModeOffButtonText = "Airplane mode" // Optional
        builder1.showAirplaneModeOffButtons = true // Optional
        noInternetDialog = builder1.build()
    }

    class SettingsFragment : PreferenceFragmentCompat(), OperationListener {

        private lateinit var viewModel: PresetViewModel

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs", MODE_PRIVATE)
            val clientId: String = prefs.getString("client_id", "")!!

            viewModel = ViewModelProviders.of(this)[PresetViewModel::class.java]
            viewModel.operationListener = this

            val userRequest: EditTextPreference = findPreference("user_request")!!
            userRequest.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                val maxLength = 4
                editText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))
            }
            userRequest.setOnPreferenceChangeListener { _, newValue ->
                val intervalPreset = IntervalPreset()
                intervalPreset.userRequest = newValue.toString().toInt()
                viewModel.onUpdateIntervalPreset(clientId, intervalPreset)
                true
            }
        }

        override fun onSuccess() {
            Toast.makeText(context, "Preference Has Been Changed", Toast.LENGTH_LONG).show()
        }

        override fun onFailure(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

    }
}