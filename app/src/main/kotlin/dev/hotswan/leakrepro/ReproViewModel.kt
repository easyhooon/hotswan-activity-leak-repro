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
    private val _event = MutableSharedFlow<ReproEvent>(extraBufferCapacity = 1)
    val event = _event.asSharedFlow()

    fun showToast() {
        _event.tryEmit(ReproEvent.ShowToast)
    }

    fun finishActivity() {
        _event.tryEmit(ReproEvent.FinishActivity)
    }
}

sealed interface ReproEvent {
    data object ShowToast : ReproEvent
    data object FinishActivity : ReproEvent
}
