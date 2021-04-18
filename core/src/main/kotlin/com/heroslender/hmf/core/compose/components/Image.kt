package com.heroslender.hmf.core.compose.components

import androidx.compose.runtime.Composable
import com.heroslender.hmf.core.ui.components.Image
import com.heroslender.hmf.core.ui.components.ImageDrawer
import com.heroslender.hmf.core.ui.components.newMeasurableGroup
import com.heroslender.hmf.core.ui.layout
import com.heroslender.hmf.core.ui.modifier.Modifier
import kotlin.math.min

//@Composable
//fun Image(
//    asset: String,
//    width: Int = -1,
//    height: Int = -1,
//    cached: Boolean = true,
//    modifier: Modifier = Modifier,
//) {
//    // TODO:
//    LocalRenderContext.current.manager.getImage(asset, width, height, cached)
//}

@Composable
fun Image(
    image: Image,
    modifier: Modifier = Modifier,
) {
    appendComposable(
        modifier.then(ImageDrawer(image)),
        {}
    ) {
        measurableGroup = newMeasurableGroup { _, constraints ->
            val width = min(image.width, constraints.maxWidth)
            val height = min(image.height, constraints.maxHeight)

            layout(width, height)
        }
    }
}