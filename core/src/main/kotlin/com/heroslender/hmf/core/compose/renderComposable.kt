package com.heroslender.hmf.core.compose

import androidx.compose.runtime.*
import com.heroslender.hmf.core.RenderContext
import com.heroslender.hmf.core.ui.ComposableNode
import com.heroslender.hmf.core.ui.modifier.Modifier
import com.heroslender.hmf.core.compose.Composable as HMFComposable
import kotlinx.coroutines.*
import java.util.concurrent.Executors

val LocalRenderContext = compositionLocalOf<RenderContext> { error("render context not provided") }
val LocalParentComposableNode = compositionLocalOf<ComposableNode> { error("There is not parent composable node provided") }

val uiThread = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
val globalSnapshotManager = GlobalSnapshotManager(uiThread)

fun renderComposable(root: ComposableNode, content: @Composable () -> Unit): Composition {
    globalSnapshotManager.ensureStarted()

    val context = DefaultMonotonicFrameClock + uiThread
    val recomposer = Recomposer(context)

    CoroutineScope(context).launch(start = CoroutineStart.UNDISPATCHED) {
        recomposer.runRecomposeAndApplyChanges()
    }

    val composition = Composition(
        applier = NodeApplier(root),
        parent = recomposer
    )
    composition.setContent @Composable {
        SetupRootCompositionLocals(root) {
            ProvideParent(root) {
                content()
            }
        }
    }

    return composition
}

@Composable
internal fun emitComposableNode(node: ComposableNode, modifier: Modifier, content: @Composable () -> Unit) {
    ComposeNode<NodeWrapper, NodeApplier>(
        factory = { NodeWrapper(node) },
        update = {
            println("ComposeNode->update()")
            //set(modifier) { this.realNode.modifier = modifier } TODO
        },
        content = { ProvideParent(node, content) }
    )
}

@Composable
fun Composable(modifier: Modifier = Modifier, content: @Composable HMFComposable.() -> Unit) {
    val composable = ComposableNode(LocalParentComposableNode.current, modifier, LocalRenderContext.current)
    emitComposableNode(composable, modifier) { composable.content() }
}

@Composable
private fun SetupRootCompositionLocals(root: ComposableNode, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalRenderContext provides root.renderContext,
        content = content
    )
}

@Composable
internal fun ProvideParent(parent: ComposableNode, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalParentComposableNode provides parent,
        content = content
    )
}