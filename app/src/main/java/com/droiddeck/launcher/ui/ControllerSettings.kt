package com.droiddeck.launcher.ui

import com.droiddeck.launcher.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.droiddeck.launcher.input.ControllerPrefs
import com.droiddeck.launcher.session.SessionPrefs

class ControllerActions(
    val onOsc: (String) -> Unit,
    val onTint: (Int) -> Unit,
    val onOpacity: (Int) -> Unit,
    val onSize: (Int) -> Unit,
    val onStickClick: (Boolean) -> Unit,
    val onAdaptiveSticks: (Boolean) -> Unit,
    val onEditLayout: () -> Unit,
    val onResetLayout: () -> Unit,
    val onMapping: () -> Unit,
    val onResetAll: () -> Unit,
)

@Composable
private fun Swatch(color: Int) {
    Box(Modifier.size(14.dp).background(Color(color), CircleShape).border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape))
}

@Composable
fun ColumnScope.ControllerRows(host: MenuHost, oscMode: String, c: ControllerPrefs.Settings, a: ControllerActions) {
    ChoiceRow(
        host, "controller-osc", stringResource(R.string.ui_3388bd75), "When the touch pad appears in a session",
        listOf(SessionPrefs.OSC_AUTO to stringResource(R.string.ui_c614ba7c), SessionPrefs.OSC_ALWAYS to stringResource(R.string.ui_a91bcce8), SessionPrefs.OSC_STEAM_QAM to stringResource(R.string.ui_766f3e27), SessionPrefs.OSC_NEVER to stringResource(R.string.ui_80c3052d)),
        oscMode, note = "Auto shows all controls without a controller. Steam + QAM shows only those buttons.", onPick = a.onOsc,
    )
    val tintOpen = host.open == "controller-tint"
    SettingsRow("Color", stringResource(R.string.ui_b33a2587), highlighted = tintOpen) {
        Box {
            ValueChip(ControllerPrefs.tints.firstOrNull { it.first == c.tint }?.second ?: stringResource(R.string.ui_081ae3fd), tintOpen) {
                host.open = if (tintOpen) null else "controller-tint"
            }
            AnchoredMenu(tintOpen, onDismiss = { if (host.open == "controller-tint") host.open = null }, title = "Color") { firstItemFocus ->
                ControllerPrefs.tints.forEachIndexed { index, (color, name) ->
                    MenuItem(name, checked = c.tint == color, leading = { Swatch(color) }, focusRequester = if (index == 0) firstItemFocus else null) {
                        a.onTint(color)
                        host.open = null
                    }
                }
            }
        }
    }
    ChoiceRow(host, "controller-opacity", "Opacity", null, ControllerPrefs.opacities.map { it to "$it%" }, c.opacity, onPick = a.onOpacity)
    ChoiceRow(host, "controller-size", "Button size", "100% keeps the standard size", ControllerPrefs.sizes.map { it to "$it%" }, c.size, onPick = a.onSize)
    ToggleRow(host, "controller-stick-click", stringResource(R.string.ui_637450c3), stringResource(R.string.ui_8346b066), c.stickClick, onChange = a.onStickClick)
    ToggleRow(host, "controller-adaptive", stringResource(R.string.ui_ebc087e5), "Sticks appear when touched near their saved positions and hide when released", c.adaptiveSticks, onChange = a.onAdaptiveSticks)
    SettingsRow("Layout", if (c.customLayout) stringResource(R.string.ui_3845db31) else "Placed for this screen's size and your grip") {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SecondaryButton(stringResource(R.string.ui_5301648d)) { a.onEditLayout() }
            SecondaryButton(stringResource(R.string.ui_44c57abd), enabled = c.customLayout) { a.onResetLayout() }
        }
    }
    val remapped = c.mapping.count { (id, target) -> id != target }
    ActionRow("Button mapping", if (remapped == 0) "Every button sends its own input" else "$remapped of ${c.mapping.size} buttons remapped", "Configure", a.onMapping)
    ActionRow(stringResource(R.string.ui_1a2afc49), "Restore the default color, opacity, size, stick behavior, mapping and layout", stringResource(R.string.ui_44c57abd), a.onResetAll)
}

@Composable
fun ControllerMappingPage(mapping: Map<String, String>, onPick: (String, String) -> Unit, onReset: () -> Unit, onBack: () -> Unit) {
    val host = rememberMenuHost()
    SettingsPage(
        host, title = stringResource(R.string.ui_a45672e9), eyebrow = stringResource(R.string.ui_a36a5ffa),
        lede = "Choose what each on-screen button sends to the game. Hidden removes the button.",
        onBack = onBack,
    ) {
        SettingsGroup(stringResource(R.string.ui_4bdafbff)) {
            for ((id, name) in ControllerPrefs.mappable) {
                ChoiceRow(host, "map-$id", name, null, ControllerPrefs.targets, mapping[id] ?: id) { onPick(id, it) }
            }
        }
        SettingsGroup("Defaults") {
            ActionRow(stringResource(R.string.ui_9c1887bc), "Every button sends its own input again", stringResource(R.string.ui_44c57abd), onReset)
        }
    }
}
