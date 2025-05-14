package com.example.hit

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class PuzzleShape(
    private val isFirst: Boolean = false,
    private val isLast: Boolean = false
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val width = size.width
                val height = size.height
                val tabSize = height * 0.25f

                moveTo(0f, 0f)
                lineTo(width * 0.3f, 0f)

                cubicTo(
                    width * 0.35f, tabSize,
                    width * 0.65f, tabSize,
                    width * 0.7f, 0f
                )
                lineTo(width, 0f)
                lineTo(width, height)

                lineTo(width * 0.7f, height)

                cubicTo(
                    width * 0.65f, height + tabSize,
                    width * 0.35f, height + tabSize,
                    width * 0.3f, height
                )

                lineTo(0f, height)
                lineTo(0f, 0f)
                close()
            })
    }
}

