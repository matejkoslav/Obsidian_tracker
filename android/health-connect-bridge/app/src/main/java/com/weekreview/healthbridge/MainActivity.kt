package com.weekreview.healthbridge

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MainActivity : ComponentActivity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val bratislavaZone = ZoneId.of("Europe/Bratislava")
    private val stepsPermission = HealthPermission.getReadPermission(StepsRecord::class)
    private val healthPermissions = setOf(stepsPermission)

    private lateinit var statusText: TextView
    private lateinit var vaultText: TextView
    private lateinit var resultText: TextView

    private val preferences by lazy {
        getSharedPreferences("week_review_health_bridge", MODE_PRIVATE)
    }

    private val vaultPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri == null) {
            setStatus("Vault folder selection was cancelled.")
            return@registerForActivityResult
        }

        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
        )
        preferences.edit().putString(KEY_VAULT_URI, uri.toString()).apply()
        updateVaultText()
        setStatus("Vault folder selected.")
    }

    private val permissionLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract(),
    ) { grantedPermissions ->
        if (grantedPermissions.contains(stepsPermission)) {
            setStatus("Health Connect steps permission granted.")
        } else {
            setStatus("Health Connect steps permission was not granted.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(buildContentView())
        updateVaultText()
        if (isPermissionRationaleIntent()) {
            showPermissionRationale()
        } else {
            refreshHealthStatus()
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun buildContentView(): View {
        statusText = TextView(this).apply { textSize = 16f }
        vaultText = TextView(this).apply { textSize = 14f }
        resultText = TextView(this).apply { textSize = 14f }

        val grantButton = Button(this).apply {
            text = "Grant Health Connect Steps Permission"
            setOnClickListener { requestStepsPermission() }
        }
        val selectFolderButton = Button(this).apply {
            text = "Select Obsidian Vault Folder"
            setOnClickListener { vaultPicker.launch(null) }
        }
        val updateButton = Button(this).apply {
            text = "Update Steps"
            setOnClickListener { updateSteps() }
        }
        val healthSettingsButton = Button(this).apply {
            text = "Open Health Connect Install/Settings"
            setOnClickListener { openHealthConnectInstallOrSettings() }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
            addView(statusText)
            addView(vaultText)
            addView(grantButton)
            addView(selectFolderButton)
            addView(updateButton)
            addView(healthSettingsButton)
            addView(resultText)
        }

        return ScrollView(this).apply { addView(layout) }
    }

    private fun refreshHealthStatus() {
        when (HealthConnectClient.getSdkStatus(this)) {
            HealthConnectClient.SDK_AVAILABLE -> setStatus("Health Connect is available.")
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED ->
                setStatus("Health Connect provider update is required before steps can be read.")
            else -> setStatus("Health Connect is unavailable. Android 13 and lower need the Health Connect app from Google Play.")
        }
    }

    private fun isPermissionRationaleIntent(): Boolean {
        return intent?.action == ACTION_SHOW_PERMISSIONS_RATIONALE ||
            intent?.action == ACTION_VIEW_PERMISSION_USAGE
    }

    private fun showPermissionRationale() {
        setStatus("This app requests Health Connect steps read access only.")
        resultText.text = "It reads today's total steps and updates the selected local vault file at logs/DD-MM-YYYY.md. It does not read other health data or send data to a server."
    }

    private fun requestStepsPermission() {
        if (!isHealthConnectAvailable()) return

        scope.launch {
            val granted = HealthConnectClient.getOrCreate(this@MainActivity)
                .permissionController
                .getGrantedPermissions()
            if (granted.contains(stepsPermission)) {
                setStatus("Health Connect steps permission is already granted.")
            } else {
                permissionLauncher.launch(healthPermissions)
            }
        }
    }

    private fun updateSteps() {
        if (!isHealthConnectAvailable()) return

        val vaultUri = preferences.getString(KEY_VAULT_URI, null)?.let(Uri::parse)
        if (vaultUri == null) {
            setStatus("Select the synced Obsidian vault folder first.")
            return
        }

        scope.launch {
            try {
                val client = HealthConnectClient.getOrCreate(this@MainActivity)
                val granted = client.permissionController.getGrantedPermissions()
                if (!granted.contains(stepsPermission)) {
                    setStatus("Grant Health Connect steps permission first.")
                    permissionLauncher.launch(healthPermissions)
                    return@launch
                }

                val today = LocalDate.now(bratislavaZone)
                val steps = readTodaySteps(client, today)
                val fileName = DailyNoteUpdater.dailyNoteFileName(today)
                val newLine = DailyNoteUpdater.buildStepsLine(steps)
                val documentStore = VaultDocumentStore(contentResolver)

                val result = withContext(Dispatchers.IO) {
                    val noteUri = documentStore.findDailyNote(vaultUri, fileName)
                        ?: return@withContext UpdateResult.Error("Missing daily note: logs/$fileName")
                    val noteText = documentStore.readText(noteUri)
                    when (val replacement = DailyNoteUpdater.replaceStepsLine(noteText, newLine)) {
                        ReplaceResult.MissingStepsLine ->
                            UpdateResult.Error("Missing Steps 🟢: line in logs/$fileName")
                        is ReplaceResult.Replaced -> {
                            documentStore.writeText(noteUri, replacement.updatedText)
                            UpdateResult.Success(fileName, replacement.oldLine, replacement.newLine)
                        }
                    }
                }

                showUpdateResult(result)
            } catch (error: Exception) {
                setStatus("Update failed: ${error.message ?: error.javaClass.simpleName}")
            }
        }
    }

    private suspend fun readTodaySteps(client: HealthConnectClient, today: LocalDate): Long {
        val startTime = today.atStartOfDay(bratislavaZone).toInstant()
        val endTime = Instant.now()
        val response = client.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
            ),
        )
        return response[StepsRecord.COUNT_TOTAL] ?: 0L
    }

    private fun showUpdateResult(result: UpdateResult) {
        when (result) {
            is UpdateResult.Error -> {
                setStatus(result.message)
                resultText.text = ""
            }
            is UpdateResult.Success -> {
                setStatus("Updated logs/${result.fileName}")
                resultText.text = "Old line:\n${result.oldLine}\n\nNew line:\n${result.newLine}"
            }
        }
    }

    private fun isHealthConnectAvailable(): Boolean {
        return when (HealthConnectClient.getSdkStatus(this)) {
            HealthConnectClient.SDK_AVAILABLE -> true
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                setStatus("Health Connect provider update is required. Use the install/settings button.")
                false
            }
            else -> {
                setStatus("Health Connect is unavailable. Use the install/settings button if this device supports it.")
                false
            }
        }
    }

    private fun openHealthConnectInstallOrSettings() {
        val marketIntent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
            setPackage("com.android.vending")
        }
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"),
        )

        runCatching { startActivity(marketIntent) }
            .recoverCatching { startActivity(webIntent) }
            .onFailure { setStatus("Could not open Health Connect install/settings flow.") }
    }

    private fun updateVaultText() {
        vaultText.text = if (preferences.contains(KEY_VAULT_URI)) {
            "Vault folder: selected"
        } else {
            "Vault folder: not selected"
        }
    }

    private fun setStatus(message: String) {
        statusText.text = message
    }

    private sealed interface UpdateResult {
        data class Success(
            val fileName: String,
            val oldLine: String,
            val newLine: String,
        ) : UpdateResult

        data class Error(val message: String) : UpdateResult
    }

    private companion object {
        const val ACTION_SHOW_PERMISSIONS_RATIONALE = "androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE"
        const val ACTION_VIEW_PERMISSION_USAGE = "android.intent.action.VIEW_PERMISSION_USAGE"
        const val KEY_VAULT_URI = "vault_tree_uri"
    }
}
