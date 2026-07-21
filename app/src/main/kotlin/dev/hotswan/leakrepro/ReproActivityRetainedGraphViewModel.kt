package dev.hotswan.leakrepro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import dev.zacsweers.metro.asContribution

class ReproActivityRetainedGraphViewModel(
    appGraph: ReproAppGraph,
) : ViewModel() {
    val graph: ReproActivityRetainedGraph =
        appGraph.asContribution<ReproActivityRetainedGraph.Factory>()
            .createActivityRetainedGraph()

    class Factory(
        private val appGraph: ReproAppGraph,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return ReproActivityRetainedGraphViewModel(appGraph) as T
        }
    }
}
