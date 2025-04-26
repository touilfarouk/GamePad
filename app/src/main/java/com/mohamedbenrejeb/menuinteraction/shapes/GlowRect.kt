package com.mohamedbenrejeb.menuinteraction.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
fun DrawScope.drawGlowRect(
    color: Color,
    frameworkPaint: NativePaint,
    paint: Paint
) {
    val rectSize = 30f
    val left = (size.width - rectSize) / 2
    val top = (size.height - rectSize) / 2

    this.drawIntoCanvas {
        val transparent = color.copy(alpha = 0f).toArgb()
        frameworkPaint.color = transparent

        frameworkPaint.setShadowLayer(10f, 0f, 0f, color.copy(alpha = 0.5f).toArgb())

        it.drawRect(
            left = left,
            top = top,
            right = left + rectSize,
            bottom = top + rectSize,
            paint = paint
        )

        drawRect(
            Color.White,
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(rectSize, rectSize),
            style = Stroke(width = 4.dp.toPx())
        )

        frameworkPaint.setShadowLayer(30f, 0f, 0f, color.copy(alpha = 0.5f).toArgb())

        it.drawRect(
            left = left,
            top = top,
            right = left + rectSize,
            bottom = top + rectSize,
            paint = paint
        )

        drawRect(
            Color.White,
            topLeft = Offset(left, top),
            size = androidx.compose.ui.geometry.Size(rectSize, rectSize),
            style = Stroke(width = 4.dp.toPx())
        )
    }
}
