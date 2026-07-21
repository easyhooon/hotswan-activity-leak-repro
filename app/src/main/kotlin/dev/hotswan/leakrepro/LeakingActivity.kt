package dev.hotswan.leakrepro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.metroViewModel

class LeakingActivity : ComponentActivity() {
    private val graphViewModel: ReproActivityRetainedGraphViewModel by viewModels {
        ReproActivityRetainedGraphViewModel.Factory(
            (application as ReproApplication).appGraph,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                CompositionLocalProvider(
                    LocalMetroViewModelFactory provides graphViewModel.graph.metroViewModelFactory,
                ) {
                    YeoBeeToastProvider {
                        val viewModel: ReproViewModel = metroViewModel()
                        val toast = LocalYeoBeeToast.current
                        ObserveAsEvents(flow = viewModel.event) { event ->
                            when (event) {
                                ReproEvent.ShowToast -> toast.showToast()
                                ReproEvent.FinishActivity -> this@LeakingActivity.finish()
                            }
                        }

                        LeakScreen(
                            onShowToast = viewModel::showToast,
                            onFinish = viewModel::finishActivity,
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun LeakScreen(
    onShowToast: () -> Unit,
    onFinish: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("The flow callback shows a custom toast, then the Activity can finish separately.")
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onShowToast,
        ) {
            Text("Show Toast")
        }
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = onFinish,
        ) {
            Text("Finish Activity")
        }
    }
}
