package dev.hotswan.leakrepro

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.GraphExtension
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@GraphExtension(ActivityRetainedScope::class)
interface ReproActivityRetainedGraph : ViewModelGraph {
    @ContributesTo(AppScope::class)
    @GraphExtension.Factory
    fun interface Factory {
        fun createActivityRetainedGraph(): ReproActivityRetainedGraph
    }
}
