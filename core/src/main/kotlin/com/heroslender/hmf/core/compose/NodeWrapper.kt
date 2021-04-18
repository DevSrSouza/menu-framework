package com.heroslender.hmf.core.compose

import com.heroslender.hmf.core.ui.ComposableNode

internal class NodeWrapper internal constructor(internal val realNode: ComposableNode) {

    fun insert(index: Int, instance: NodeWrapper) {
        realNode.insertChild(index, instance.realNode)
    }

    fun remove(index: Int, count: Int) {
        repeat(count) {
            realNode.removeChild(index)
        }
    }

    fun move(from: Int, to: Int, count: Int) {
        if (from > to) {
            repeat(count) {
                realNode.insertChild(to + it, realNode.children[from + it])
            }
        } else {
            repeat(count) {
                realNode.insertChild(to, realNode.children[from])
            }
        }
    }
}