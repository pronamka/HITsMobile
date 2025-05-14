package com.example.hit

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

@Composable
fun Drag(
    block: CodeBlock,
    position: BlockPosition,
    active: Boolean,
    initID : () -> Unit,
    positionChange: (BlockPosition) -> Unit,
){
    var X by remember { mutableStateOf(position.posX) }
    var Y by remember { mutableStateOf(position.posY) }

    Box(
        modifier = Modifier
            .offset { IntOffset(X.roundToInt(), Y.roundToInt()) }
            .zIndex(if (active) 1f else 0f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { initID() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        X += dragAmount.x
                        Y += dragAmount.y
                        positionChange(position.copy(posX = X, posY = Y))
                    }
                )
            }
    ) {
        BlockItem(
            block = block,
            isFirst = false,
            isLast = false,
            onClick = { }
        )
    }
}
