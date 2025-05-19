package com.example.hit

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
private fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

@Composable
fun Drag(
    block: CodeBlock,
    position: BlockPosition,
    active: Boolean,
    initID : () -> Unit,
    positionChange: (BlockPosition) -> Unit,
    allBlockPositions: Map<Int, BlockPosition>
){
    var X by remember { mutableStateOf(position.posX) }
    var Y by remember { mutableStateOf(position.posY) }
    
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val blockHeight = 150f

    var isNearSnap by remember { mutableStateOf(false) }
    var snapTarget by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    val animatedY by animateFloatAsState(
        targetValue = if (snapTarget != null) snapTarget!!.second else Y,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "snapAnimation"
    )

    fun checkSnapTargets(currentY: Float): Pair<Float, Float>? {
        var closestSnap: Pair<Float, Float>? = null
        var minDistance = Float.MAX_VALUE

        allBlockPositions.forEach { (id, pos) ->
            if (id != position.id) {
                val blockBottom = pos.posY + blockHeight
                val blockTop = pos.posY

                val topDistance = abs(currentY + blockHeight - blockTop)
                if (topDistance < 50 && topDistance < minDistance) {
                    minDistance = topDistance
                    closestSnap = Pair(pos.posX, blockTop - blockHeight)
                }

                val bottomDistance = abs(currentY - blockBottom)
                if (bottomDistance < 50 && bottomDistance < minDistance) {
                    minDistance = bottomDistance
                    closestSnap = Pair(pos.posX, blockBottom)
                }
            }
        }
        return closestSnap
    }
    
    Box(
        modifier = Modifier
            .offset { IntOffset(X.roundToInt(), if (snapTarget != null) animatedY.roundToInt() else Y.roundToInt()) }
            .zIndex(if (active) 1f else 0f)
            .drawBehind {
                if (isNearSnap) {
                    drawRect(
                        color = Color(0x33FFFFFF),
                        size = size
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        initID()
                        snapTarget = null 
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newX = (X + dragAmount.x).coerceAtLeast(0f)
                        val newY = Y + dragAmount.y
                        

                        val potentialSnap = checkSnapTargets(newY)
                        isNearSnap = potentialSnap != null
                        
                        X = newX
                        Y = newY
                        positionChange(position.copy(posX = X, posY = Y))
                    },
                    onDragEnd = {
                        val finalSnap = checkSnapTargets(Y)
                        if (finalSnap != null) {
                            snapTarget = finalSnap
                            X = finalSnap.first
                            Y = finalSnap.second
                            positionChange(position.copy(posX = X, posY = Y))
                        }
                        isNearSnap = false
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
