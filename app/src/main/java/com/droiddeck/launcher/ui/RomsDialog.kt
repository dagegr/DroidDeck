package com.droiddeck.launcher.ui

import com.droiddeck.launcher.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun RomsDialog(path: String?, onChoose: () -> Unit, onClear: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ui_8ecf29be)) },
        text = {
            Column {
                Text(
                    stringResource(R.string.ui_2f540328),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    path ?: stringResource(R.string.ui_d1868186),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (path != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.ui_7c7721bb),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = { TextButton(onClick = onChoose) { Text(if (path == null) stringResource(R.string.ui_1838a415) else stringResource(R.string.ui_2de7d622)) } },
        dismissButton = {
            if (path != null) TextButton(onClick = onClear) { Text(stringResource(R.string.ui_03d5d801)) }
            else TextButton(onClick = onDismiss) { Text(stringResource(R.string.ui_77dfd213)) }
        },
    )
}
