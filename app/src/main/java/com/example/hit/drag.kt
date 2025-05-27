package com.example.hit

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import com.example.hit.blocks.BasicBlock
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Drag(
    block: BasicBlock,
    position: BlockPosition,
    active: Boolean,
    initID : () -> Unit,
    positionChange: (BlockPosition) -> Unit,
    allBlockPositions: Map<UUID, BlockPosition>,
    blocksOnScreen: List<BasicBlock>,
    topPanelHeight: Float,
    onHeightChanged: (Dp) -> Unit,
    del: ()-> Unit
){
    val density = LocalDensity.current
    var X by remember { mutableStateOf(position.posX) }
    var Y by remember { mutableStateOf(position.posY) }

    var blockHeight by remember { mutableStateOf(with(density) { block.heightDP.toPx() }) }
    var showDelete by remember { mutableStateOf(false) }

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

    fun findSnapTarget(
        currentBlockId: UUID,
        currentX: Float,
        currentY: Float,
        blockHeight: Float,
        blockPositions: Map<UUID, BlockPosition>
    ): Pair<Float, Float>? {
        val snapThreshold = 50f
        var minDistance = Float.MAX_VALUE
        var snapPosition: Pair<Float, Float>? = null

        for ((id, pos) in blockPositions) {
            if (id == currentBlockId) continue

            val otherTop = pos.posY
            val otherBottom = pos.posY + blockHeight

            val distanceToTop = Math.abs((currentY + blockHeight) - otherTop)
            if (distanceToTop < snapThreshold && distanceToTop < minDistance && Math.abs(currentX - pos.posX) < snapThreshold) {
                minDistance = distanceToTop
                snapPosition = Pair(pos.posX, otherTop - blockHeight)
            }


            val distanceToBottom = Math.abs(currentY - (pos.posY + blockHeight))
            if (distanceToBottom < snapThreshold && distanceToBottom < minDistance && Math.abs(currentX - pos.posX) < snapThreshold) {
                minDistance = distanceToBottom
                snapPosition = Pair(pos.posX, pos.posY + blockHeight)
            }
        }

        return snapPosition
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(X.roundToInt(), if (snapTarget != null) animatedY.roundToInt() else Y.roundToInt()) }
            .zIndex(if (active) 1f else 0f)
            .combinedClickable(
                onClick = {},
                onLongClick = { showDelete = true }
            )
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
                        val newY = (Y + dragAmount.y)

                        X = newX
                        Y = newY
                        position.posX = newX
                        position.posY = newY
                        positionChange(position.copy(posX = X, posY = Y))
                        val potentialSnap = findSnapTarget(block.id, newX, newY, blockHeight, allBlockPositions)
                        snapTarget = potentialSnap
                        isNearSnap = potentialSnap != null
                    },
                    onDragEnd = {
                        val finalSnap = findSnapTarget(block.id, X, Y, blockHeight, allBlockPositions)
                        if (finalSnap != null) {
                            X = finalSnap.first
                            Y = finalSnap.second
                            position.posX = X
                            position.posY = Y
                            positionChange(position.copy(posX = X, posY = Y))
                        }
                        snapTarget = null
                        isNearSnap = false
                    }
                )
            }
    ) {
        BlockItem(
            block = block,
            onClick = { },
            onHeightChanged = { newHeightDp -> blockHeight = with(density) { newHeightDp.toPx() } }
        )
        if(showDelete){
            Button(
                onClick = { del() },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(text = "Delete",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font
                )
            }
        }
    }
}
