package com.heroslender.hmf.core.compose.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.font.FontStyle
import com.heroslender.hmf.core.ui.components.TextDrawer
import com.heroslender.hmf.core.ui.components.newMeasurableGroup
import com.heroslender.hmf.core.ui.layout
import com.heroslender.hmf.core.ui.modifier.Modifier
import kotlin.math.min

@Composable
fun Label(
    text: String,
    style: FontStyle,
    modifier: Modifier = Modifier,
) {
    appendComposable(modifier.then(TextDrawer(text, style)), {}) {
        measurableGroup = newMeasurableGroup { _, constraints ->
            val width = min(style.font.getWidth(text), constraints.maxWidth)
            val height = min(style.font.height, constraints.maxHeight)

            layout(width, height)
        }
    }
}