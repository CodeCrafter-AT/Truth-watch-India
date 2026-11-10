package org.truthwatchindia.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.truthwatchindia.app.ui.navigation.AppNavHost
import org.truthwatchindia.app.ui.theme.TruthWatchTheme

class MainActivity : ComponentActivity() {

    private var initialArticleId by mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIntent(intent)

        setContent {
            TruthWatchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(initialArticleId = initialArticleId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        // 1. Direct notification extra
        val extraId = intent.getLongExtra("article_id", -1L)
        if (extraId > 0) {
            initialArticleId = extraId
            return
        }

        // 2. Deep link URL handler
        val data: Uri? = intent.data
        if (data != null) {
            val lastSegment = data.lastPathSegment
            val parsedId = lastSegment?.toLongOrNull()
            if (parsedId != null && parsedId > 0) {
                initialArticleId = parsedId
            }
        }
    }
}
