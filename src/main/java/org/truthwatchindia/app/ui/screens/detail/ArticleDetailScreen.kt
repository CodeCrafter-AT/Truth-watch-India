package org.truthwatchindia.app.ui.screens.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import org.truthwatchindia.app.data.model.WpPost
import org.truthwatchindia.app.ui.components.ErrorMessage
import org.truthwatchindia.app.ui.components.HtmlText
import org.truthwatchindia.app.ui.components.LoadingIndicator
import org.truthwatchindia.app.ui.theme.AccentBlue
import org.truthwatchindia.app.ui.theme.CanvasWhite
import org.truthwatchindia.app.ui.theme.CrimsonMaroon
import org.truthwatchindia.app.ui.theme.DarkCharcoal
import org.truthwatchindia.app.ui.theme.DividerStroke
import org.truthwatchindia.app.ui.theme.FacebookBlue
import org.truthwatchindia.app.ui.theme.MetadataGray
import org.truthwatchindia.app.ui.theme.SurfaceLight
import org.truthwatchindia.app.ui.theme.TwitterBlack
import org.truthwatchindia.app.ui.theme.WhatsAppGreen
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    postId: Long,
    onBack: () -> Unit
) {
    val viewModel: ArticleDetailViewModel = viewModel(
        key = "article_$postId",
        factory = ArticleDetailViewModel.Factory(postId)
    )
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Font size scale controls
    var fontSizeMultiplier by remember { mutableFloatStateOf(1.0f) }
    var showFontMenu by remember { mutableStateOf(false) }

    // Full-screen image viewer state
    var isImageViewerOpen by remember { mutableStateOf(false) }
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    // Text To Speech state
    var isTtsPlaying by remember { mutableStateOf(false) }
    var isTtsReady by remember { mutableStateOf(false) }
    var ttsInstance by remember { mutableStateOf<TextToSpeech?>(null) }

    // Setup TextToSpeech
    DisposableEffect(context) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = ttsInstance?.setLanguage(Locale("hi", "IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    ttsInstance?.language = Locale.US
                }
                isTtsReady = true
            }
        }
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                isTtsPlaying = true
            }

            override fun onDone(utteranceId: String?) {
                isTtsPlaying = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                isTtsPlaying = false
            }
        })
        ttsInstance = tts

        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    val scrollState = rememberScrollState()
    val readingProgress = if (scrollState.maxValue > 0) {
        (scrollState.value.toFloat() / scrollState.maxValue.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Truth Watch India",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            ttsInstance?.stop()
                            onBack()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        // Font Size Picker
                        Box {
                            IconButton(onClick = { showFontMenu = true }) {
                                Icon(
                                    imageVector = Icons.Default.FormatSize,
                                    contentDescription = "फ़ॉन्ट आकार",
                                    tint = Color.White
                                )
                            }
                            DropdownMenu(
                                expanded = showFontMenu,
                                onDismissRequest = { showFontMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("छोटा (Small - 85%)") },
                                    onClick = {
                                        fontSizeMultiplier = 0.85f
                                        showFontMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("सामान्य (Default - 100%)") },
                                    onClick = {
                                        fontSizeMultiplier = 1.0f
                                        showFontMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("बड़ा (Large - 118%)") },
                                    onClick = {
                                        fontSizeMultiplier = 1.18f
                                        showFontMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("अतिरिक्त बड़ा (XL - 135%)") },
                                    onClick = {
                                        fontSizeMultiplier = 1.35f
                                        showFontMenu = false
                                    }
                                )
                            }
                        }

                        // Bookmark toggle
                        IconButton(onClick = {
                            viewModel.toggleBookmark()
                            val msg = if (!state.isBookmarked) "बुकमार्क में सहेजा गया" else "बुकमार्क हटाया गया"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = if (state.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = Color.White
                            )
                        }

                        // Native Share Action
                        IconButton(onClick = {
                            val post = state.post
                            if (post != null) {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${post.getCleanTitle()}\n\nपूरा समाचार पढ़ें:\n${post.link}"
                                    )
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Share Article"))
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = CrimsonMaroon,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )

                // Reading Progress Bar
                LinearProgressIndicator(
                    progress = { readingProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = AccentBlue,
                    trackColor = CrimsonMaroon.copy(alpha = 0.3f),
                )
            }
        },
        containerColor = CanvasWhite
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.errorMessage != null && state.post == null -> {
                    ErrorMessage(
                        message = state.errorMessage ?: "",
                        onRetry = { viewModel.loadArticle() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.post != null -> {
                    val post = state.post!!
                    val cleanBodyText = remember(post.content.rendered) {
                        WpPost.cleanHtml(post.content.rendered)
                    }
                    val wordCount = remember(cleanBodyText) {
                        cleanBodyText.split("\\s+".toRegex()).count { it.isNotBlank() }
                    }
                    val readTimeMin = remember(wordCount) {
                        maxOf(1, wordCount / 180)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(bottom = 32.dp)
                    ) {
                        // Offline notice banner if viewing an offline copy
                        if (state.isOfflineCopy) {
                            Surface(
                                color = Color(0xFFFFF3CD),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = Color(0xFF856404),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "ऑफ़लाइन प्रतिलिपि — आप सहेजा हुआ समाचार पढ़ रहे हैं",
                                        color = Color(0xFF856404),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Category Tag
                        Surface(
                            color = CrimsonMaroon,
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        ) {
                            Text(
                                text = post.getCategoryName(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        // Article Title: Scaled cleanly
                        Text(
                            text = post.getCleanTitle(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = (21 * fontSizeMultiplier).sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = (28 * fontSizeMultiplier).sp,
                                color = DarkCharcoal
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )

                        // Author, Date & Reading Time Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CrimsonMaroon),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.getAuthorName().take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = post.getAuthorName(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = DarkCharcoal
                                    )
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = MetadataGray,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${post.getFormattedDate()} (IST)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = MetadataGray
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = MetadataGray,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "~$readTimeMin मिनट वाचन",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            color = MetadataGray
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Audio Reader (TTS) Toolbar
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = SurfaceLight,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DividerStroke)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = null,
                                        tint = CrimsonMaroon,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isTtsPlaying) "समाचार वाचन जारी है..." else "समाचार सुनें (Listen)",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DarkCharcoal
                                        )
                                        Text(
                                            text = "हिंदी व अंग्रेज़ी ऑडियो वाचन",
                                            fontSize = 10.sp,
                                            color = MetadataGray
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (!isTtsPlaying) {
                                        IconButton(
                                            onClick = {
                                                if (ttsInstance != null && isTtsReady) {
                                                    val speechText = "${post.getCleanTitle()}. $cleanBodyText"
                                                    // Chunk text into 3500 chars to satisfy engine limits
                                                    val chunks = speechText.chunked(3500)
                                                    ttsInstance?.stop()
                                                    chunks.forEachIndexed { index, chunk ->
                                                        val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                                                        ttsInstance?.speak(chunk, queueMode, null, "utterance_$index")
                                                    }
                                                    isTtsPlaying = true
                                                } else {
                                                    Toast.makeText(context, "ऑडियो इंजन तैयार हो रहा है...", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Play Audio",
                                                tint = CrimsonMaroon,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    } else {
                                        IconButton(
                                            onClick = {
                                                ttsInstance?.stop()
                                                isTtsPlaying = false
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Pause,
                                                contentDescription = "Pause Audio",
                                                tint = CrimsonMaroon,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                ttsInstance?.stop()
                                                isTtsPlaying = false
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Stop,
                                                contentDescription = "Stop Audio",
                                                tint = DarkCharcoal,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Social Share Button Strip (WhatsApp, X, Facebook, Copy Link)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // WhatsApp
                            ShareButton(
                                label = "WhatsApp",
                                backgroundColor = WhatsAppGreen,
                                contentColor = Color.White,
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://api.whatsapp.com/send?text=${Uri.encode(post.getCleanTitle() + " " + post.link)}")
                                    }
                                    context.startActivity(sendIntent)
                                }
                            )

                            // X / Twitter
                            ShareButton(
                                label = "X / Twitter",
                                backgroundColor = TwitterBlack,
                                contentColor = Color.White,
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://twitter.com/intent/tweet?text=${Uri.encode(post.getCleanTitle())}&url=${Uri.encode(post.link)}")
                                    }
                                    context.startActivity(sendIntent)
                                }
                            )

                            // Facebook
                            ShareButton(
                                label = "Facebook",
                                backgroundColor = FacebookBlue,
                                contentColor = Color.White,
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse("https://www.facebook.com/sharer/sharer.php?u=${Uri.encode(post.link)}")
                                    }
                                    context.startActivity(sendIntent)
                                }
                            )

                            // Copy Link
                            ShareButton(
                                label = "Copy",
                                backgroundColor = DividerStroke,
                                contentColor = DarkCharcoal,
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Article Link", post.link)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "लिंक कॉपी किया गया", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Full-width featured media image with tap-to-expand
                        val imageUrl = post.getFeaturedImageUrl()
                        if (!imageUrl.isNullOrBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedImageUrl = imageUrl
                                        isImageViewerOpen = true
                                    }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = post.getCleanTitle(),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(16f / 9f)
                                )

                                // Tap to enlarge indicator badge
                                Surface(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ZoomIn,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "बड़ा देखें",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Article Body with clean HTML rendering & custom font multiplier
                        HtmlText(
                            html = post.content.rendered,
                            fontSizeMultiplier = fontSizeMultiplier,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider(
                            thickness = 1.dp,
                            color = DividerStroke,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Truth Watch India — निष्पक्ष पत्रकारिता, तथ्य और सत्य",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MetadataGray,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }

    // Full-screen Image Viewer Dialog with pinch-to-zoom & pan
    if (isImageViewerOpen && !selectedImageUrl.isNullOrBlank()) {
        Dialog(
            onDismissRequest = { isImageViewerOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            var scale by remember { mutableFloatStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }
            val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
                scale = (scale * zoomChange).coerceIn(1f, 4f)
                val extraWidth = (scale - 1) * 1000
                val extraHeight = (scale - 1) * 1000
                val maxX = extraWidth / 2
                val maxY = extraHeight / 2
                offset = Offset(
                    x = (offset.x + offsetChange.x).coerceIn(-maxX, maxX),
                    y = (offset.y + offsetChange.y).coerceIn(-maxY, maxY)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(selectedImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Full Screen Image",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = transformableState)
                )

                // Close button
                IconButton(
                    onClick = { isImageViewerOpen = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareButton(
    label: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
