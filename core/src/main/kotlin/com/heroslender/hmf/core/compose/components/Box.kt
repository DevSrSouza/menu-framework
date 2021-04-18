package com.heroslender.hmf.core.compose.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.ui.Alignment
import com.heroslender.hmf.core.ui.components.EmptyMeasurableGroup
import com.heroslender.hmf.core.ui.components.boxMeasurableGroup
import com.heroslender.hmf.core.ui.modifier.Modifier

@Composable
fun Box(
    modifier: Modifier,
    alignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit,
) {
    appendComposable(modifier, content) {
        measurableGroup = boxMeasurableGroup(alignment)
    }
}

@Composable
fun Box(
    modifier: Modifier,
) {
    appendComposable(modifier, {}) {
        measurableGroup = EmptyMeasurableGroup
    }
}
