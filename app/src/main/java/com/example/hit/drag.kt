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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.BlockType
import java.nio.file.WatchEvent
import java.util.UUID
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


@Composable
fun Drag(
    block: BasicBlock,
    position: BlockPosition,
    active: Boolean,
    initID : () -> Unit,
    positionChange: (BlockPosition) -> Unit,
    allBlockPositions: Map<UUID, BlockPosition>,
    listOfBlocks: List<BasicBlock>,
    topPanelHeight: Float
){
    var X by remember { mutableStateOf(position.posX) }
    var Y by remember { mutableStateOf(position.posY) }

    val blockHeight = with (LocalDensity.current) { block.heightDP.toPx() }

    var isNearSnap by remember { mutableStateOf(false) }
    var snapTarget by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    val animatedY by animateFloatAsState(
        targetValue = if (snapTarget != null) snapTarget!!.second else Y,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )
    fun compatible(draggedBlockId: UUID, possibleConnectedBlockId: UUID, isTop : Boolean): Boolean {
        val draggedBlock = listOfBlocks.first { it.id == draggedBlockId }
        val possibleConnectedBlock = listOfBlocks.first{ it.id == possibleConnectedBlockId }
        return if (isTop) {
            draggedBlock.isBottomCompatible(possibleConnectedBlock)
        } else {
            draggedBlock.isTopCompatible(possibleConnectedBlock)
        }
    }

    fun createConnection(draggedBlockId: UUID, possibleConnectedBlockId: UUID, isTop : Boolean) {
        val draggedBlock = listOfBlocks.first { it.id == draggedBlockId }
        val possibleConnectedBlock = listOfBlocks.first { it.id == possibleConnectedBlockId }

        if (isTop) {
            draggedBlock.connectTopBlock(possibleConnectedBlock)
        } else {
            draggedBlock.connectBottomBlock(possibleConnectedBlock)
        }
    }


    data class Snap(
        var position: Pair<Float, Float>?,
        var blockID : UUID?,
        var isTop : Boolean,
        )

    fun checkSnapTargets(currentY: Float, currentX: Float): Snap {
        var minDistance = Float.MAX_VALUE
        val closestSnap = Snap(null, null, false)

        allBlockPositions.forEach { (id, pos) ->
            if (id != block.id) {
                val blockBottom = pos.posY + blockHeight
                val blockTop = pos.posY

                val bottomToTopDistance = abs((currentY + blockHeight) - blockTop)
                if (bottomToTopDistance < 50 && bottomToTopDistance < minDistance && abs(currentX - pos.posX) < 50) {
                    if (compatible(block.id, id, true)) {
                        minDistance = bottomToTopDistance
                        closestSnap.position = Pair(pos.posX, blockTop - blockHeight)
                        closestSnap.blockID = id
                        closestSnap.isTop = true
                    }
                }

                val topToBottomDistance = abs(currentY - blockBottom)
                if (topToBottomDistance < 50 && topToBottomDistance < minDistance && abs(currentX - pos.posX) < 50) {
                    if (compatible(block.id, id, false)) {
                        minDistance = topToBottomDistance
                        closestSnap.position = Pair(pos.posX, blockTop - blockHeight)
                        closestSnap.blockID = id
                        closestSnap.isTop = false
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
                        block.move()
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
                        val potentialSnap = checkSnapTargets(newY, newX)
                        isNearSnap = potentialSnap.blockID != null
                    },
                    onDragEnd = {
                        val final = checkSnapTargets(Y, X)

                        if (final.position != null) {
                            createConnection(block.id, final.blockID!!, final.isTop)
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
