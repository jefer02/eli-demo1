package com.elyndra.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.elyndra.app.R
import com.elyndra.app.domain.model.InstalledEmulatorApp
import com.elyndra.app.domain.model.Platform

@Composable
fun EmulatorPickerDialog(
    platform: Platform,
    installedEmulators: List<InstalledEmulatorApp>,
    onDismiss: () -> Unit,
    onSelect: (String?) -> Unit,
) {
    var customPackageName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.emulator_dialog_title, platform.displayName)) },
        text = {
            Column {
                if (installedEmulators.isEmpty()) {
                    Text(stringResource(R.string.emulator_none_detected))
                } else {
                    installedEmulators.forEach { app ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            RadioButton(
                                selected = platform.emulatorPackageName == app.packageName,
                                onClick = { onSelect(app.packageName) },
                            )
                            Text(app.label, modifier = Modifier.padding(top = 12.dp))
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.emulator_manual_package),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 12.dp),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                ) {
                    OutlinedTextField(
                        value = customPackageName,
                        onValueChange = { customPackageName = it },
                        placeholder = { Text("com.example.emulator") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        onClick = { onSelect(customPackageName.trim()) },
                        enabled = customPackageName.isNotBlank(),
                    ) { Text(stringResource(R.string.action_use)) }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSelect(null) }) { Text(stringResource(R.string.action_clear)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        },
    )
}
