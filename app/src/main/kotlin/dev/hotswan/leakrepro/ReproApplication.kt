package dev.hotswan.leakrepro

import android.app.Application
import dev.zacsweers.metro.createGraphFactory

class ReproApplication : Application() {
    val appGraph: ReproAppGraph by lazy {
        createGraphFactory<ReproAppGraph.Factory>().create()
    }
}
