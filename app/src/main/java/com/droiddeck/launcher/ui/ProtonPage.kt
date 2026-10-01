package com.droiddeck.launcher.ui

import com.droiddeck.launcher.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class ProtonRow(val id: String, val name: String, val installed: String?, val queued: Boolean)

@Composable
fun ProtonPage(
    rows: List<ProtonRow>,
    busyId: String?,
    stage: String?,
    percent: Int,
    runtimeReady: Boolean,
    sessionRunning: Boolean,
    onInstall: (String) -> Unit,
    onCancel: (String) -> Unit,
    onRemove: (String) -> Unit,
    onBack: () -> Unit,
) {
    val host = rememberMenuHost()
    val colors = MaterialTheme.colorScheme
    SettingsPage(
        host,
        title = stringResource(R.string.ui_4541a5ea),
        eyebrow = stringResource(R.string.ui_cdd7bb28),
        lede = stringResource(R.string.ui_f444c0b1),
        onBack = onBack,
    ) {
        if (!runtimeReady) Text(
            stringResource(R.string.ui_e78eb0e4),
            fontSize = 12.sp, color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
        )
        if (sessionRunning) Text(
            stringResource(R.string.ui_87049406),
            fontSize = 12.sp, color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp, bottom = 6.dp),
        )
        SettingsGroup(stringResource(R.string.ui_152fee2a)) {
            for (row in rows) {
                SettingsRow(
                    row.name,
                    when {
                        row.installed != null -> "Installed ${row.installed}"
                        row.queued -> stringResource(R.string.ui_ded63f8f)
                        else -> stringResource(R.string.ui_1aab9f18)
                    },
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        when {
                            row.installed != null -> SecondaryButton(stringResource(R.string.ui_e963907d), enabled = busyId == null && runtimeReady && !sessionRunning) { onRemove(row.id) }
                            busyId != null -> SecondaryButton(if (busyId == row.id) stringResource(R.string.ui_8d278823) else stringResource(R.string.ui_fd6c3ebf), enabled = false) {}
                            else -> SecondaryButton(stringResource(R.string.ui_8607e4ba), enabled = runtimeReady && !sessionRunning) { onInstall(row.id) }
                        }
                        if (row.queued && row.installed == null && busyId == null) {
                            SecondaryButton(stringResource(R.string.ui_cf675711), enabled = !sessionRunning) { onCancel(row.id) }
                        }
                    }
                }
                if (busyId == row.id) Column(modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 10.dp)) {
                    Text(
                        if (stage != null && percent >= 0) "$stage · $percent%" else stage ?: "Starting…",
                        fontSize = 13.sp, color = colors.onSurfaceVariant, modifier = Modifier.padding(bottom = 5.dp),
                    )
                    if (percent >= 0) LinearProgressIndicator(progress = { percent / 100f }, modifier = Modifier.fillMaxWidth().height(4.dp))
                    else LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(4.dp))
                }
            }
        }
    }
}
