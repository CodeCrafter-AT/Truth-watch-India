package org.truthwatchindia.app.ui.screens.team

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.truthwatchindia.app.ui.theme.CanvasWhite
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.DividerStroke
import org.truthwatchindia.app.ui.theme.MetadataGray
import org.truthwatchindia.app.ui.theme.SurfaceLight

data class TeamMember(
    val name: String,
    val role: String,
    val initial: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamScreen(
    onBack: () -> Unit
) {
    val teamMembers = listOf(
        TeamMember("अभिषेक मिश्रा", "संपादक (Editor)", "अ"),
        TeamMember("विनीत कुमार वर्मा", "सह संपादक (Co-Editor)", "व"),
        TeamMember("अभिषेक त्रिपाठी", "स्टेट कॉर्डिनेटर (State Coordinator)", "अ"),
        TeamMember("गौरव शुक्ला", "एसोसिएट एडिटर (Associate Editor)", "ग"),
        TeamMember("राघवेंद्र द्विवेदी", "लीगल एडवाइज़र (Legal Advisor)", "र"),
        TeamMember("सुबोध द्विवेदी", "कंटेंट क्रियेटर - English (Content Creator)", "स"),
        TeamMember("RJ प्रियंका", "रेडियो जॉकी (Radio Jockey)", "R"),
        TeamMember("पंकज शुक्ला", "विज्ञापन प्रबंधक (Advertising Manager)", "प")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "हमारी टीम (Our Team)",
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
        containerColor = SurfaceLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            item {
                Text(
                    text = "Truth Watch India — सम्पादकीय मंडल",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkCharcoal
                    ),
                    modifier = Modifier.padding(bottom = 12.dp, top = 4.dp)
                )
            }

            items(teamMembers) { member ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CanvasWhite),
                    border = BorderStroke(1.dp, DividerStroke),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CrimsonMaroon),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = member.initial,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = DarkCharcoal
                                )
                            )
                            Text(
                                text = member.role,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MetadataGray,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
