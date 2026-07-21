package dev.hotswan.leakrepro

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

/** Small Activity-bound custom-toast wrapper with lifecycle cleanup. */
class ReproToast(
    activity: ComponentActivity,
) : DefaultLifecycleObserver {
    private var activity: ComponentActivity? = activity

    init {
        activity.lifecycle.addObserver(this)
    }

    fun showToast() {
        activity?.let { Toast.makeText(it, "Trip registered", Toast.LENGTH_SHORT).show() }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        activity = null
        owner.lifecycle.removeObserver(this)
    }
}

