package org.truthwatchindia.app.ui.screens.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.truthwatchindia.app.data.api.RetrofitClient
import org.truthwatchindia.app.ui.components.HtmlText
import org.truthwatchindia.app.ui.components.LoadingIndicator
import org.truthwatchindia.app.ui.theme.CanvasWhite
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    var contentHtml by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getPage(128) // About Us Page ID
            if (response.isSuccessful && response.body() != null) {
                contentHtml = response.body()?.content?.rendered
            }
        } catch (_: Exception) {
            // Keep default fallback
        } finally {
            isLoading = false
        }
    }

    val fallbackText = """
        <p><strong>Truth Watch India (TWI)</strong> एक स्वतंत्र, निष्पक्ष और विश्वसनीय समाचार संस्थान है, जिसका उद्देश्य देश और समाज से जुड़ी हर महत्वपूर्ण खबर को बिना किसी पक्षपात के जनता तक पहुँचाना है।</p>
        <p>हमारा विश्वास है कि पत्रकारिता का मूल उद्देश्य केवल खबर दिखाना नहीं, बल्कि सत्य, तथ्य और जनहित को प्राथमिकता देना है।</p>
        <p>हम राजनीति, सामाजिक मुद्दे, अपराध, खेल, मनोरंजन, व्यापार और जनसरोकार से जुड़ी खबरों पर पैनी नजर रखते हैं और जमीनी हकीकत को सामने लाने के लिए प्रतिबद्ध हैं।</p>
        <p><strong>हमारा संकल्प:</strong><br>
        • बिना किसी डर या दबाव के सच्ची पत्रकारिता करना।<br>
        • फेक न्यूज और भ्रामक जानकारियों के खिलाफ तथ्यों पर आधारित रिपोर्टिंग।<br>
        • जनता की आवाज को सत्ता और प्रशासन तक पहुंचाना।</p>
        <p>Truth Watch India — सत्य की खोज, आपकी आवाज।</p>
    """.trimIndent()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "हमारे बारे में (About Us)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CrimsonMaroon,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = CanvasWhite
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Truth Watch India",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HtmlText(
                        html = contentHtml ?: fallbackText
                    )
                }
            }
        }
    }
}
