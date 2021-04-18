package com.heroslender.hmf.core.compose

import androidx.compose.runtime.AbstractApplier
import com.heroslender.hmf.core.ui.ComposableNode

internal class NodeApplier(root: ComposableNode) : AbstractApplier<NodeWrapper>(NodeWrapper(root)) {
    override fun onClear() {
        // no-op
        println("NodeApplier.onClear()")
    }

    override fun insertBottomUp(index: Int, instance: NodeWrapper) {
        println("NodeApplier.insertBottomUp()")
        current.insert(index, instance)
    }

    override fun insertTopDown(index: Int, instance: NodeWrapper) {}

    override fun move(from: Int, to: Int, count: Int) {
        println("NodeApplier.move()")
        current.move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        println("NodeApplier.remove()")
        current.remove(index, count)
    }
}