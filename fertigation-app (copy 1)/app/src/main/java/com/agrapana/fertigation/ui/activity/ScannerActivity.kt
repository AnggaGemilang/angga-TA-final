package com.agrapana.fertigation.ui.activity

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.agrapana.fertigation.databinding.ActivityScannerBinding
import com.journeyapps.barcodescanner.CaptureActivity

class ScannerActivity : CaptureActivity()