package dev.hotswan.leakrepro

import androidx.lifecycle.ViewModel
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@ViewModelKey
@ContributesIntoMap(ActivityRetainedScope::class)
@Inject
class ReproViewModel : ViewModel() {
    private val _finishEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val finishEvent = _finishEvent.asSharedFlow()

    fun finishActivity() {
        _finishEvent.tryEmit(Unit)
    }
}
