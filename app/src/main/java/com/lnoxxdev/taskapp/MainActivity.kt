package com.lnoxxdev.taskapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lnoxxdev.taskapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        getNotificationPermission()
        setupNavigation()
    }

    private fun getNotificationPermission(){
        val rememberNotificationAllow = getPreferences(Context.MODE_PRIVATE).getBoolean(
            getString(R.string.pref_remember_notification_allow),
            true
        )
        if (rememberNotificationAllow) notificationPermissionLaunch()
    }

    private fun notificationPermissionLaunch() {
        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (!it) showNotificationDialog()
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showNotificationDialog() {
        MaterialAlertDialogBuilder(this)
            .setIcon(R.drawable.ic_notification)
            .setTitle(R.string.notification_permission_dialog_title)
            .setMessage(R.string.notification_permission_required)
            .setPositiveButton(R.string.allow) { _, _ ->
                val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNeutralButton(R.string.notification_dont_remember) {  _, _ ->
                val sharedPref = getPreferences(Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.pref_remember_notification_allow), false)
                    apply()
                }
            }
            .show()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavView.setupWithNavController(navController)
    }
}