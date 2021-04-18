package com.heroslender.hmf.core.compose.components

import androidx.compose.runtime.remember
import com.heroslender.hmf.core.compose.Composable
import com.heroslender.hmf.core.ui.Composable
import com.heroslender.hmf.core.ui.modifier.Modifier
import androidx.compose.runtime.Composable as ComposeComposable

@ComposeComposable
internal fun appendComposable(
    modifier: Modifier,
    content: @ComposeComposable () -> Unit,
    transformer: Composable.() -> Unit = {},
) {
    Composable(modifier) {
        remember { transformer() } //not call every single recomposition
        content()
    }
}