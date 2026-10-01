package com.droiddeck.launcher.ui

import com.droiddeck.launcher.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.droiddeck.launcher.session.SessionPrefs

class CoreRow(val core: Int, val label: String)

@Composable
fun PerformancePage(
    cores: List<CoreRow>,
    clientOverride: Boolean,
    clientCores: Set<Int>,
    gameCores: Set<Int>,
    tuSysmem: Boolean,
    zinkLazy: Boolean,
    glThread: Boolean,
    noGlError: Boolean,
    noXalia: Boolean,
    gamescopeRealtime: Boolean,
    prootNoSeccomp: Boolean,
    guestHostname: String,
    phantomWarning: String?,
    onClientOverride: (Boolean) -> Unit,
    onTuSysmem: (Boolean) -> Unit,
    onZinkLazy: (Boolean) -> Unit,
    onGlThread: (Boolean) -> Unit,
    onNoGlError: (Boolean) -> Unit,
    onNoXalia: (Boolean) -> Unit,
    onGamescopeRealtime: (Boolean) -> Unit,
    onProotNoSeccomp: (Boolean) -> Unit,
    onGuestHostname: (String) -> Unit,
    onClientCore: (Int, Boolean) -> Unit,
    onGameCore: (Int, Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val host = rememberMenuHost()
    val colors = MaterialTheme.colorScheme
    val coreItems = cores.map { it.core to it.label }
    SettingsPage(
        host, title = stringResource(R.string.ui_63c90455),
        lede = stringResource(R.string.ui_4eb4402e),
        onBack = onDismiss,
    ) {
        SettingsGroup(stringResource(R.string.ui_c507e304)) {
            ToggleRow(
                host, "override", stringResource(R.string.ui_73ed7ef0),
                "Pins Steam, its UI helper, and gamescope to the cores below. Reapplied every few seconds.",
                clientOverride, onChange = onClientOverride,
            )
            MultiRow(
                host, "clientCores", stringResource(R.string.ui_4e0c5af3), if (clientOverride) stringResource(R.string.ui_a0c0993f) else "Enable the override to choose.",
                coreItems, clientCores, enabled = clientOverride, onToggle = onClientCore,
            )
        }
        SettingsGroup(stringResource(R.string.ui_7edbacb6)) {
            MultiRow(
                host, "gameCores", stringResource(R.string.ui_7edbacb6),
                stringResource(R.string.ui_ddbe491c),
                coreItems, gameCores, onToggle = onGameCore,
            )
        }
        SettingsGroup(stringResource(R.string.ui_6be3f4d6)) {
            ToggleRow(
                host, "glthread", stringResource(R.string.ui_dd4c5bde),
                "May improve Steam menu responsiveness. Device impact is unverified.",
                glThread, onChange = onGlThread,
            )
            ToggleRow(
                host, "zink", stringResource(R.string.ui_173c7662),
                "Lazy, compact descriptors. Recommended for drivers without descriptor buffers.",
                zinkLazy, onChange = onZinkLazy,
            )
            ToggleRow(
                host, "noglerror", stringResource(R.string.ui_491520f8),
                "Disables per-call GL validation.",
                noGlError, onChange = onNoGlError,
            )
            ToggleRow(
                host, "gsrealtime", "gamescope: realtime GPU queue",
                "Gives the compositor's GPU work priority over the game's. Can smooth frame pacing, but can cost games GPU time.",
                gamescopeRealtime, onChange = onGamescopeRealtime,
            )
        }
        SettingsGroup(stringResource(R.string.ui_223c7c03)) {
            ToggleRow(
                host, "sysmem", stringResource(R.string.ui_0b295a58),
                "On by default: faster for the Steam interface and most games. Required on Adreno 710/720/722.",
                tuSysmem, onChange = onTuSysmem,
            )
            ToggleRow(
                host, "xalia", stringResource(R.string.ui_e5b170e1),
                "Disables Proton's gamepad navigation helper, which costs every game CPU time. Turn off only if a game needs it.",
                noXalia, onChange = onNoXalia,
            )
            ToggleRow(
                host, "seccomp", stringResource(R.string.ui_3d206a49),
                "May fix missing syscall errors on some kernels, but can reduce performance.",
                prootNoSeccomp, onChange = onProotNoSeccomp,
            )
        }
        SettingsGroup(stringResource(R.string.ui_1fcb337b)) {
            var draft by remember(guestHostname) { mutableStateOf(guestHostname) }
            val valid = SessionPrefs.validGuestHostname(draft) != null
            SettingsRow(
                stringResource(R.string.ui_67da05e8),
                if (valid || draft.isBlank()) "What the session and Steam report as this machine's name. Blank restores ${SessionPrefs.DEFAULT_GUEST_HOSTNAME}."
                else "Letters, digits and inner hyphens only, up to 63 characters. Not saved until it is valid.",
            ) {
                OutlinedTextField(
                    draft, { v ->
                        draft = v.take(63)
                        if (draft.isBlank() || SessionPrefs.validGuestHostname(draft) != null) onGuestHostname(draft)
                    },
                    singleLine = true, isError = !valid && draft.isNotBlank(),
                    placeholder = { Text(SessionPrefs.DEFAULT_GUEST_HOSTNAME) },
                    modifier = Modifier.width(220.dp),
                )
            }
        }
        if (phantomWarning != null) {
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(colors.error.copy(alpha = 0.08f))
                    .border(1.dp, colors.error.copy(alpha = 0.4f), RoundedCornerShape(12.dp)).padding(12.dp),
            ) {
                Text(stringResource(R.string.ui_617c81a9), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = colors.error)
                Text(phantomWarning, fontSize = 12.sp, color = colors.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}
