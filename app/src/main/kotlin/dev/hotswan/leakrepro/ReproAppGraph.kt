package dev.hotswan.leakrepro

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph

@DependencyGraph(scope = AppScope::class)
interface ReproAppGraph {
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(): ReproAppGraph
    }
}
