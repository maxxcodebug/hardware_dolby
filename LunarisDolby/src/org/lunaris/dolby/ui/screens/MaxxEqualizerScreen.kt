/*
 * Copyright (C) 2026 Anshuman_X (maxxcodebug)
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lunaris.dolby.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.lunaris.dolby.ui.components.ModernSettingSlider
import org.lunaris.dolby.ui.components.SpectrumView
import org.lunaris.dolby.ui.components.ModernSettingSwitch
import org.lunaris.dolby.ui.components.ModernSettingsCard
import org.lunaris.dolby.ui.viewmodel.MaxxEqualizerViewModel

private fun formatFrequency(hz: Float): String {
    return if (hz >= 1000f) {
        val khz = hz / 1000f
        if (khz == khz.toInt().toFloat()) "${khz.toInt()} kHz" else "$khz kHz"
    } else {
        "${hz.toInt()} Hz"
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MaxxEqualizerScreen(
    viewModel: MaxxEqualizerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val spectrum by viewModel.spectrum.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Bands", "MBC", "Limiter")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "MaxxEqualizer",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            SpectrumView(bars = spectrum, modifier = Modifier.fillMaxWidth())
        }

        item {
            var showSaveDialog by remember { mutableStateOf(false) }
            var presetName by remember { mutableStateOf("") }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.presetNames) { name ->
                        AssistChip(
                            onClick = { viewModel.loadPreset(name) },
                            label = { Text(name) },
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.deletePreset(name) }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete $name")
                                }
                            }
                        )
                    }
                }
                IconButton(onClick = { showSaveDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Save preset")
                }
            }

            if (showSaveDialog) {
                AlertDialog(
                    onDismissRequest = { showSaveDialog = false },
                    title = { Text("Save Preset") },
                    text = {
                        OutlinedTextField(
                            value = presetName,
                            onValueChange = { presetName = it },
                            label = { Text("Preset name") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.savePreset(presetName)
                            presetName = ""
                            showSaveDialog = false
                        }) { Text("Save") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
                    }
                )
            }
        }

        item {
            ModernSettingSwitch(
                title = "Enable MaxxEqualizer",
                subtitle = "Independent system equalizer",
                checked = state.enabled,
                onCheckedChange = { viewModel.setEnabled(it) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            SecondaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            item {
                AnimatedVisibility(
                    visible = state.enabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ModernSettingsCard(title = "Preamp", icon = Icons.Default.VolumeUp) {
                        ModernSettingSlider(
                            title = "Preamp",
                            value = state.preampDb.toInt(),
                            valueRange = -20f..20f,
                            steps = 39,
                            onValueChange = { viewModel.setPreamp(it) },
                            valueLabel = { "$it dB" }
                        )
                    }
                }
            }

            items(state.bandFrequencies.indices.toList()) { index ->
                AnimatedVisibility(
                    visible = state.enabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ModernSettingsCard(
                        title = formatFrequency(state.bandFrequencies[index]),
                        icon = Icons.Default.Equalizer
                    ) {
                        ModernSettingSlider(
                            title = formatFrequency(state.bandFrequencies[index]),
                            value = state.bandGains.getOrElse(index) { 0f }.toInt(),
                            valueRange = -20f..20f,
                            steps = 39,
                            onValueChange = { viewModel.setBandGain(index, it) },
                            valueLabel = { "$it dB" }
                        )
                    }
                }
            }
        } else if (selectedTab == 1) {
            item {
                ModernSettingSwitch(
                    title = "Enable Multiband Compressor",
                    subtitle = "3-band dynamics control (Low / Mid / High)",
                    checked = state.mbcEnabled,
                    onCheckedChange = { viewModel.setMbcEnabled(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(state.mbcBands.indices.toList()) { index ->
                val band = state.mbcBands[index]
                AnimatedVisibility(
                    visible = state.mbcEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ModernSettingsCard(title = "${band.label} Band", icon = Icons.Default.Tune) {
                        ModernSettingSlider(
                            title = "Threshold",
                            value = band.threshold.toInt(),
                            valueRange = -60f..0f,
                            steps = 59,
                            onValueChange = { viewModel.setMbcThreshold(index, it) },
                            valueLabel = { "$it dB" }
                        )
                        ModernSettingSlider(
                            title = "Ratio",
                            value = band.ratio.toInt(),
                            valueRange = 1f..20f,
                            steps = 18,
                            onValueChange = { viewModel.setMbcRatio(index, it) },
                            valueLabel = { "${it}:1" }
                        )
                        ModernSettingSlider(
                            title = "Attack",
                            value = band.attackMs.toInt(),
                            valueRange = 1f..200f,
                            steps = 198,
                            onValueChange = { viewModel.setMbcAttack(index, it) },
                            valueLabel = { "$it ms" }
                        )
                        ModernSettingSlider(
                            title = "Release",
                            value = band.releaseMs.toInt(),
                            valueRange = 10f..1000f,
                            steps = 98,
                            onValueChange = { viewModel.setMbcRelease(index, it) },
                            valueLabel = { "$it ms" }
                        )
                    }
                }
            }
        } else {
            item {
                ModernSettingSwitch(
                    title = "Enable Limiter",
                    subtitle = "Final-stage output limiter, prevents clipping",
                    checked = state.limiterEnabled,
                    onCheckedChange = { viewModel.setLimiterEnabled(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                AnimatedVisibility(
                    visible = state.limiterEnabled,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    ModernSettingsCard(title = "Limiter", icon = Icons.Default.Tune) {
                        ModernSettingSlider(
                            title = "Threshold",
                            value = state.limiterThreshold.toInt(),
                            valueRange = -30f..0f,
                            steps = 29,
                            onValueChange = { viewModel.setLimiterThreshold(it) },
                            valueLabel = { "$it dB" }
                        )
                        ModernSettingSlider(
                            title = "Ratio",
                            value = state.limiterRatio.toInt(),
                            valueRange = 1f..20f,
                            steps = 18,
                            onValueChange = { viewModel.setLimiterRatio(it) },
                            valueLabel = { "${it}:1" }
                        )
                        ModernSettingSlider(
                            title = "Release",
                            value = state.limiterRelease.toInt(),
                            valueRange = 1f..1000f,
                            steps = 98,
                            onValueChange = { viewModel.setLimiterRelease(it) },
                            valueLabel = { "$it ms" }
                        )
                        ModernSettingSlider(
                            title = "Post Gain",
                            value = state.limiterPostGain.toInt(),
                            valueRange = -20f..20f,
                            steps = 39,
                            onValueChange = { viewModel.setLimiterPostGain(it) },
                            valueLabel = { "$it dB" }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}
