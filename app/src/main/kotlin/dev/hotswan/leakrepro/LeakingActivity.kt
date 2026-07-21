package dev.hotswan.leakrepro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableSharedFlow

class LeakingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                val events = remember { MutableSharedFlow<Unit>(extraBufferCapacity = 1) }
                YeoBeeToastProvider {
                    val toast = LocalYeoBeeToast.current
                    ObserveAsEvents(flow = events) {
                        toast.showToast()
                        this@LeakingActivity.finish()
                    }

                    LeakScreen(onClose = { events.tryEmit(Unit) })
                }
            }
        }
    }
}
@Composable
private fun LeakScreen(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("The flow callback shows a custom toast and finishes this Activity.")
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onClose,
        ) {
            Text("Finish Activity")
        }
    }
}
