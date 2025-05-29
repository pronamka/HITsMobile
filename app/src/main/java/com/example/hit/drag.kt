import com.example.hit.BlockItem
import com.example.hit.BlockPosition
import com.example.hit.font
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
    onSizeChanged: (UUID, Float, Float) -> Unit,
    del: ()-> Unit,
    blockWithDeleteShownId: UUID?,
    onShowDeleteChange: (UUID?) -> Unit
){
    fun compatible(draggedBlockId: UUID, possibleConnectedBlockId: UUID, isTop : Boolean): Boolean {
        val draggedBlock = blocksOnScreen.first { it.id == draggedBlockId }
        val possibleConnectedBlock = blocksOnScreen.first{ it.id == possibleConnectedBlockId }
        return if (isTop) {
            draggedBlock.isBottomCompatible(possibleConnectedBlock)
        } else {
            draggedBlock.isTopCompatible(possibleConnectedBlock)
        }
    }

    fun createConnection(draggedBlockId: UUID, possibleConnectedBlockId: UUID, isTop : Boolean) {
        val draggedBlock = blocksOnScreen.first { it.id == draggedBlockId }
        val possibleConnectedBlock = blocksOnScreen.first { it.id == possibleConnectedBlockId }

        if (isTop) {
            draggedBlock.connectTopBlock(possibleConnectedBlock)
        } else {
            draggedBlock.connectBottomBlock(possibleConnectedBlock)
        }
    }
    
    val density = LocalDensity.current
    val minBlockY = 20f

    var X by remember { mutableStateOf(position.posX) }
    var Y by remember { mutableStateOf(position.posY) }

    var blockHeight by remember { mutableStateOf(0f) }
    var blockWidth by remember { mutableStateOf(0f) }

    var isNearSnap by remember { mutableStateOf(false) }
    var snapTarget by remember { mutableStateOf<Pair<Float, Float>?>(null) }

    val animatedY by animateFloatAsState(
        targetValue = if (snapTarget != null) snapTarget!!.second else Y,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )


    fun distance(point1: Pair<Float, Float>, point2: Pair<Float, Float>): Float {
        val dx = point1.first - point2.first
        val dy = point1.second - point2.second
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    fun findSnapTarget(
        currentBlockId: UUID,
        currentX: Float,
        currentY: Float,
        blockHeight: Float,
        blockWidth: Float,
        blockPositions: Map<UUID, BlockPosition>
    ): Pair<Float, Float>? {
        for ((id, pos) in blockPositions) {
            if (id == currentBlockId) continue

            val otherHeightPx = pos.heightPx
            val otherWidthPx = pos.widthPx
            val otherTop = pos.posY
            val otherBottom = pos.posY + otherHeightPx

            val otherTopCenter = Pair(pos.posX + otherWidthPx / 2, otherTop)
            val otherBottomCenter = Pair(pos.posX + otherWidthPx / 2, otherBottom)

            val currentBottomCenter = Pair(currentX + blockWidth / 2, currentY + blockHeight)
            val currentTopCenter = Pair(currentX + blockWidth / 2, currentY)

            val distanceBottomToTop = distance(currentBottomCenter, otherTopCenter)
            if (distanceBottomToTop < 50f) {
                return Pair(otherBottomCenter.first - blockWidth / 2, otherBottomCenter.second - blockHeight)
            }

            val distanceTopToBottom = distance(currentTopCenter, otherBottomCenter)
            if (distanceTopToBottom < 50f) {
                return Pair(otherBottomCenter.first - blockWidth / 2, otherBottomCenter.second)
            }
        }

        return null
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(X.roundToInt(), animatedY.roundToInt()) }
            .zIndex(if (active) 1f else 0f)
            .combinedClickable(
                onClick = { onShowDeleteChange(null) },
                onLongClick = { onShowDeleteChange(block.id) }
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
                        val potentialSnap = findSnapTarget(block.id, newX, newY, blockHeight, blockWidth, allBlockPositions)
                        snapTarget = potentialSnap
                        isNearSnap = potentialSnap != null
                    },
                    onDragEnd = {
                        val finalY = if (snapTarget != null) snapTarget!!.second else Y

                        X = if (snapTarget != null) snapTarget!!.first else X
                        Y = finalY

                        positionChange(position.copy(posX = X, posY = Y, heightPx = blockHeight, widthPx = blockWidth))

                        snapTarget = null
                        isNearSnap = false
                    }
                )
            }
    ) {
        BlockItem(
            block = block,
            onClick = { onShowDeleteChange(null) },
            onSizeChanged = { newWidthPx, newHeightPx ->
                blockWidth = newWidthPx
                blockHeight = newHeightPx
                onSizeChanged(block.id, newWidthPx, newHeightPx)
            }
        )
        if(block.id == blockWithDeleteShownId){
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
