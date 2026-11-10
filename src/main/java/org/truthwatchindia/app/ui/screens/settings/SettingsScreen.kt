package org.truthwatchindia.app.ui.screens.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.truthwatchindia.app.TruthWatchApp
import org.truthwatchindia.app.ui.theme.CanvasWhite
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.DividerStroke
import org.truthwatchindia.app.ui.theme.MetadataGray
import org.truthwatchindia.app.ui.theme.SurfaceLight
import java.io.File
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesRepo = remember { TruthWatchApp.instance.preferencesRepository }
    val newsRepo = remember { TruthWatchApp.instance.repository }

    val notificationsEnabled by preferencesRepo.notificationsEnabled.collectAsState(initial = true)
    val fontScale by preferencesRepo.fontScale.collectAsState(initial = 1.0f)
    val fontSizeLabel by preferencesRepo.fontSizeLabel.collectAsState(initial = "Medium")

    var showFontDialog by remember { mutableStateOf(false) }
    var cacheSizeMb by remember { mutableStateOf(getCacheSizeMb(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "सेटिंग्स एवं जानकारी (Settings)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CrimsonMaroon,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = SurfaceLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Preferences Section
            SettingsSectionHeader("प्राथमिकताएं (Preferences)")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CanvasWhite),
                border = BorderStroke(1.dp, DividerStroke),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column {
                    // Notifications Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = CrimsonMaroon,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "ब्रेकिंग न्यूज़ अलर्ट",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = DarkCharcoal
                                    )
                                )
                                Text(
                                    text = "ताज़ा व महत्वपूर्ण घटनाओं की सूचनाएं",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MetadataGray)
                                )
                            }
                        }

                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { isChecked ->
                                scope.launch {
                                    preferencesRepo.setNotificationsEnabled(isChecked)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CrimsonMaroon
                            )
                        )
                    }

                    HorizontalDivider(color = DividerStroke, thickness = 0.5.dp)

                    // Font Size Preference
                    SettingsNavRowWithSubtitle(
                        icon = Icons.Default.FormatSize,
                        title = "फ़ॉन्ट का आकार (Font Size)",
                        subtitle = "वर्तमान: $fontSizeLabel",
                        onClick = { showFontDialog = true }
                    )

                    HorizontalDivider(color = DividerStroke, thickness = 0.5.dp)

                    // Clear Cache Action
                    SettingsActionRow(
                        icon = Icons.Default.Delete,
                        title = "कैश साफ़ करें (Clear Cache)",
                        subtitle = "सहेजी गई छवियां एवं ऑफ़लाइन लेख हटाएं ($cacheSizeMb MB)",
                        onClick = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    context.cacheDir.deleteRecursively()
                                    newsRepo.clearArticleCache()
                                }
                                cacheSizeMb = getCacheSizeMb(context)
                                Toast.makeText(context, "कैश सफलतापूर्वक साफ़ किया गया", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Editorial & About Section
            SettingsSectionHeader("संपादकीय एवं संपर्क (About TWI)")

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = CanvasWhite),
                border = BorderStroke(1.dp, DividerStroke),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column {
                    SettingsNavRow(
                        icon = Icons.Default.Info,
                        title = "हमारे बारे में (About Us)",
                        onClick = { onNavigate("about") }
                    )
                    HorizontalDivider(color = DividerStroke, thickness = 0.5.dp)

                    SettingsNavRow(
                        icon = Icons.Default.People,
                        title = "हमारी टीम (Our Team)",
                        onClick = { onNavigate("team") }
                    )
                    HorizontalDivider(color = DividerStroke, thickness = 0.5.dp)

                    SettingsNavRow(
                        icon = Icons.Default.Mail,
                        title = "संपर्क करें (Contact Us)",
                        onClick = { onNavigate("contact") }
                    )
                    HorizontalDivider(color = DividerStroke, thickness = 0.5.dp)

                    SettingsNavRow(
                        icon = Icons.Default.Language,
                        title = "वेबसाइट पर देखें (Visit Website)",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://truthwatchindia.org/"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Identity & Version Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Truth Watch India",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CrimsonMaroon
                    )
                )
                Text(
                    text = "सत्यमेव जयते • लखनऊ व नई दिल्ली",
                    style = MaterialTheme.typography.bodySmall.copy(color = MetadataGray)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "वर्जन 1.0.0 (Production)",
                    style = MaterialTheme.typography.labelSmall.copy(color = MetadataGray)
                )
                Text(
                    text = "प्रेस काउंसिल ऑफ इंडिया मानकों के अनुरूप",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MetadataGray,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }

    // Font Size Selection Dialog
    if (showFontDialog) {
        val options = listOf(
            Triple(0.85f, "Small", "छोटा (Small - 85%)"),
            Triple(1.0f, "Medium", "सामान्य (Medium - 100%)"),
            Triple(1.18f, "Large", "बड़ा (Large - 118%)"),
            Triple(1.35f, "Extra Large", "अतिरिक्त बड़ा (XL - 135%)")
        )

        AlertDialog(
            onDismissRequest = { showFontDialog = false },
            title = {
                Text(
                    text = "फ़ॉन्ट का आकार चुनें",
                    fontWeight = FontWeight.Bold,
                    color = DarkCharcoal
                )
            },
            text = {
                Column {
                    options.forEach { (scale, label, displayName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        preferencesRepo.setFontScale(scale, label)
                                    }
                                    showFontDialog = false
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (fontSizeLabel == label),
                                onClick = {
                                    scope.launch {
                                        preferencesRepo.setFontScale(scale, label)
                                    }
                                    showFontDialog = false
                                },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = CrimsonMaroon
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = displayName,
                                fontSize = 14.sp,
                                color = DarkCharcoal
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFontDialog = false }) {
                    Text("रद्द करें", color = CrimsonMaroon)
                }
            }
        )
    }
}

private fun getCacheSizeMb(context: Context): String {
    return try {
        val cacheSize = context.cacheDir.walkTopDown().sumOf { it.length() }
        String.format(Locale.US, "%.1f", cacheSize / (1024.0 * 1024.0))
    } catch (_: Exception) {
        "0.0"
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = CrimsonMaroon,
            fontSize = 13.sp
        ),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CrimsonMaroon,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkCharcoal
                )
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MetadataGray,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SettingsNavRowWithSubtitle(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CrimsonMaroon,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = DarkCharcoal
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = MetadataGray)
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MetadataGray,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CrimsonMaroon,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DarkCharcoal
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(color = MetadataGray)
            )
        }
    }
}
