package com.mohamedbenrejeb.menuinteraction.shapes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
fun DrawScope.drawGlowTriangle(
    color: Color,
    frameworkPaint: NativePaint,
    paint: Paint,
) {
    val triangleSize = 30f
    val centerX = size.width / 2
    val centerY = size.height / 2

    val points = listOf(
        Offset(centerX, centerY - triangleSize / 2),
        Offset(centerX - triangleSize / 2, centerY + triangleSize / 2),
        Offset(centerX + triangleSize / 2, centerY + triangleSize / 2),
        Offset(centerX, centerY - triangleSize / 2),
    )

    this.drawIntoCanvas {
        val transparent = color.copy(alpha = 0f).toArgb()
        frameworkPaint.color = transparent

        frameworkPaint.setShadowLayer(10f, 0f, 0f, color.copy(alpha = 0.5f).toArgb())

        it.drawPoints(
            points = points,
            pointMode = PointMode.Polygon,
            paint = paint
        )

        drawPoints(
            points = points,
            color = Color.White,
            pointMode = PointMode.Polygon,
            strokeWidth = 4.dp.toPx()
        )

        frameworkPaint.setShadowLayer(30f, 0f, 0f, color.copy(alpha = 0.5f).toArgb())

        it.drawPoints(
            points = points,
            pointMode = PointMode.Polygon,
            paint = paint
        )

        drawPoints(
            points = points,
            color = Color.White,
            pointMode = PointMode.Polygon,
            strokeWidth = 4.dp.toPx()
        )
    }
}
