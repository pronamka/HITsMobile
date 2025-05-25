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
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import com.example.hit.blocks.BasicBlock
import java.nio.file.WatchEvent
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun Drag(
    block: BasicBlock,
    position: BlockPosition,
    active: Boolean,
    initID : () -> Unit,
    positionChange: (BlockPosition) -> Unit,
    allBlockPositions: Map<UUID, BlockPosition>,
    blocksOnScreen: List<BasicBlock>
){
    var X by remember { mutableStateOf(position.posX) }
    var Y by remember { mutableStateOf(position.posY) }

    val blockHeight = 124f

    var isNearSnap by remember { mutableStateOf(false) }
    var snapTarget by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    val animatedY by animateFloatAsState(
        targetValue = if (snapTarget != null) snapTarget!!.second else Y,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )
    fun compatible(draggedBlockId: UUID, possibleConnectedBlockId: UUID): Boolean {
        val draggedBlock = blocksOnScreen.firstOrNull { it.id == draggedBlockId }
        val possibleConnectedBlock = blocksOnScreen.firstOrNull { it.id == possibleConnectedBlockId }

        return draggedBlock!!.compatibleBlocks.contains(possibleConnectedBlock!!.type) &&
                possibleConnectedBlock.connectionCnt < 2
    }

    fun createConnection(firstBlockId: UUID, secondBlockId: UUID) {
        val firstBlock = blocksOnScreen.firstOrNull { it.id == firstBlockId }
        val secondBlock = blocksOnScreen.firstOrNull { it.id == secondBlockId }
        firstBlock!!.connectionCnt++
        secondBlock!!.connectionCnt++
    }

    data class Snap(var position: Pair<Float, Float>?, var blockID : UUID?)

    fun checkSnapTargets(currentY: Float, currentX: Float): Snap {
        var minDistance = Float.MAX_VALUE
        val closestSnap = Snap(null, null)

        allBlockPositions.forEach { (id, pos) ->
            if (id != block.id) {
                val blockBottom = pos.posY + blockHeight
                val blockTop = pos.posY

                val bottomToTopDistance = abs((currentY + blockHeight) - blockTop)
                if (bottomToTopDistance < 50 && bottomToTopDistance < minDistance && abs(currentX - pos.posX) < 50) {
                    if (compatible(block.id, id)) {
                        minDistance = bottomToTopDistance
                        closestSnap.position = Pair(pos.posX, blockTop - blockHeight)
                        closestSnap.blockID = id
                    }
                }

                val topToBottomDistance = abs(currentY - blockBottom)
                if (topToBottomDistance < 50 && topToBottomDistance < minDistance && abs(currentX - pos.posX) < 50) {
                    if (compatible(block.id, id)) {
                        minDistance = topToBottomDistance
                        closestSnap.position = Pair(pos.posX, blockTop - blockHeight)
                        closestSnap.blockID = id
                    }
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
                        block.connectionCnt = 0
                        initID()
                        snapTarget = null
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newX = (X + dragAmount.x).coerceAtLeast(0f)
                        val newY = (Y + dragAmount.y).coerceIn(88f, Float.POSITIVE_INFINITY)

                        X = newX
                        Y = newY
                        position.posX = newX
                        position.posY = newY
                        positionChange(position.copy(posX = X, posY = Y))
                        val potentialSnap = checkSnapTargets(newY, newX)
                        isNearSnap = potentialSnap != null
                    },
                    onDragEnd = {
                        val final = checkSnapTargets(Y, X)

                        if (final.position != null) {
                            createConnection(block.id, final.blockID!!)
                            snapTarget = final.position
                            X = final.position!!.first
                            Y = final.position!!.second
                            position.posX = X
                            position.posY = Y
                            positionChange(position.copy(posX = X, posY = Y))
                        }
                        isNearSnap = false
                    }
                )
            }
    ) {
        BlockItem(
            block = block,
            onClick = { }
        )
    }
}
