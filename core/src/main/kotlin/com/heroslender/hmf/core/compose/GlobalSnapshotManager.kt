package com.heroslender.hmf.core.compose

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.snapshots.ObserverHandle
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Platform-specific mechanism for starting a monitor of global snapshot state writes
 * in order to schedule the periodic dispatch of snapshot apply notifications.
 * This process should remain platform-specific; it is tied to the threading and update model of
 * a particular platform and framework target.
 *
 * Composition bootstrapping mechanisms for a particular platform/framework should call
 * [ensureStarted] during setup to initialize periodic global snapshot notifications.
 */
class GlobalSnapshotManager(coroutineDispatcher: CoroutineDispatcher) {
    private var started = AtomicBoolean(false)
    private var commitPending = false
    private var removeWriteObserver: (ObserverHandle)? = null

    private val scheduleScope = CoroutineScope(coroutineDispatcher + SupervisorJob())

    @OptIn(ExperimentalComposeApi::class)
    fun ensureStarted() {
        if (started.compareAndSet(false, true)) {
            removeWriteObserver = Snapshot.registerGlobalWriteObserver(globalWriteObserver)
        }
    }

    @OptIn(ExperimentalComposeApi::class)
    private val globalWriteObserver: (Any) -> Unit = {
        // Race, but we don't care too much if we end up with multiple calls scheduled.
        if (!commitPending) {
            commitPending = true
            schedule {
                commitPending = false
                Snapshot.sendApplyNotifications()
            }
        }
    }

    private val scheduledCallbacks = mutableListOf<() -> Unit>()

    private var isSynchronizeScheduled = false

    private fun synchronize() {
        scheduledCallbacks.forEach { it.invoke() }
        scheduledCallbacks.clear()
        isSynchronizeScheduled = false
    }

    private fun schedule(block: () -> Unit) {
        scheduledCallbacks.add(block)
        if (!isSynchronizeScheduled) {
            isSynchronizeScheduled = true
            scheduleScope.launch { synchronize() }
        }
    }
}