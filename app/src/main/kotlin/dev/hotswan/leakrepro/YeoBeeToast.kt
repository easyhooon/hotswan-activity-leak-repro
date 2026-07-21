package dev.hotswan.leakrepro

import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class YeoBeeToast(
    activity: ComponentActivity,
) {
    private var activity: ComponentActivity? = activity
    private var windowManager: WindowManager? = activity.windowManager

    private var currentView: ComposeView? = null
    private var currentJob: Job? = null
    private var isDisposed = false
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            dispose()
        }
    }

    init {
        activity.lifecycle.addObserver(lifecycleObserver)
    }

    fun showToast(text: String = "Trip registered") {
        if (isDisposed) return
        val activity = activity ?: return
        val windowManager = windowManager ?: return
        dismissCurrent()

        val composeView = ComposeView(activity).apply {
            setContent {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    Surface {
                        Text(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            text = text,
                        )
                    }
                }
            }
            setViewTreeLifecycleOwner(activity)
            setViewTreeSavedStateRegistryOwner(activity)
            setViewTreeViewModelStoreOwner(activity)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT,
        ).apply {
            windowAnimations = android.R.style.Animation_Toast
            gravity = Gravity.TOP or Gravity.FILL_HORIZONTAL
            y = 36.dp.value.toInt()
        }

        runCatching { windowManager.addView(composeView, params) }
            .onSuccess {
                currentView = composeView
                currentJob = activity.lifecycleScope.launch {
                    delay(2000L)
                    dismissCurrent()
                }
            }
    }

    fun dispose() {
        if (isDisposed) return
        isDisposed = true
        dismissCurrent()
        activity?.lifecycle?.removeObserver(lifecycleObserver)
        activity = null
        windowManager = null
    }

    private fun dismissCurrent() {
        currentJob?.cancel()
        currentJob = null
        val windowManager = windowManager
        currentView?.let { view ->
            if (view.isAttachedToWindow && windowManager != null) {
                runCatching { windowManager.removeView(view) }
            }
            view.disposeComposition()
        }
        currentView = null
    }
}

val LocalYeoBeeToast: ProvidableCompositionLocal<YeoBeeToast> =
    staticCompositionLocalOf {
        error("YeoBeeToast not provided. Wrap content with YeoBeeToastProvider.")
    }

@Composable
fun YeoBeeToastProvider(
    content: @Composable () -> Unit,
) {
    val activity = requireNotNull(LocalActivity.current) {
        "YeoBeeToastProvider requires a ComponentActivity context."
    } as ComponentActivity
    val toast = remember(activity) { YeoBeeToast(activity) }
    DisposableEffect(toast) {
        onDispose { toast.dispose() }
    }
    CompositionLocalProvider(LocalYeoBeeToast provides toast, content = content)
}
