package com.heroslender.hmf.core.compose.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.ui.Alignment
import com.heroslender.hmf.core.ui.Arrangement
import com.heroslender.hmf.core.ui.Orientation
import com.heroslender.hmf.core.ui.components.orientedCopmonentMeasurableGroup
import com.heroslender.hmf.core.ui.modifier.Modifier

@Composable
fun Row(
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Horizontal.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Vertical.Top,
    content: @Composable () -> Unit,
) {
    appendComposable(modifier, content) {
        measurableGroup = orientedCopmonentMeasurableGroup(Orientation.HORIZONTAL, horizontalArrangement, verticalAlignment)
    }
}

@Composable
fun Column(
    modifier: Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Vertical.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Horizontal.Start,
    content: @Composable () -> Unit,
) {
    appendComposable(modifier, content) {
        measurableGroup = orientedCopmonentMeasurableGroup(Orientation.VERTICAL, verticalArrangement, horizontalAlignment)
    }
}