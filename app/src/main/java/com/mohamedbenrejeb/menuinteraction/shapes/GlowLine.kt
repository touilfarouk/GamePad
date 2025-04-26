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
fun DrawScope.drawGlowLine(
    color: Color,
    frameworkPaint: NativePaint,
    paint: Paint,
    start: Offset,
    end: Offset,
) {
    val center = Offset(size.width / 2, size.height / 2)
    val halfLine = 15f

    val newStart = Offset(center.x - halfLine, center.y)
    val newEnd = Offset(center.x + halfLine, center.y)

    this.drawIntoCanvas {
        val transparent = color.copy(alpha = 0f).toArgb()
        frameworkPaint.color = transparent

        frameworkPaint.setShadowLayer(10f, 0f, 0f, color.copy(alpha = 0.5f).toArgb())

        it.drawLine(
            p1 = newStart,
            p2 = newEnd,
            paint = paint
        )

        drawLine(
            color = Color.White,
            start = newStart,
            end = newEnd,
            strokeWidth = 4.dp.toPx()
        )

        frameworkPaint.setShadowLayer(30f, 0f, 0f, color.copy(alpha = 0.5f).toArgb())

        it.drawLine(
            p1 = newStart,
            p2 = newEnd,
            paint = paint
        )

        drawLine(
            color = Color.White,
            start = newStart,
            end = newEnd,
            strokeWidth = 4.dp.toPx()
        )
    }
}
