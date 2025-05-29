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
import kotlin.math.roundToInt
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Drag(
    block: BasicBlock,
    active : Boolean,
    initID : () -> Unit,
    blocksOnScreen: List<BasicBlock>,
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

    var isNearSnap by remember { mutableStateOf(false) }
    var snapTarget by remember { mutableStateOf<Pair<Float, Float>?>(null) }


    val animatedY by animateFloatAsState(
        targetValue = if (snapTarget != null) snapTarget!!.second else block.Y.value,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )


    fun distance(point1: Pair<Float, Float>, point2: Pair<Float, Float>): Float {
        //Log.println(Log.DEBUG, null, listOf(point1, point2).toString())
        val dx = point1.first - point2.first
        val dy = point1.second - point2.second
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    fun findSnapTarget(
        currentBlock: BasicBlock
    ): Pair<Float, Float>? {
        for (otherBlock in blocksOnScreen) {
            if (otherBlock.id == currentBlock.id) continue

            val otherHeightPx = otherBlock.getDynamicHeightPx(density)
            val otherWidthPx = otherBlock.getDynamicWidthPx(density)
            val otherTop = otherBlock.Y.value
            val otherBottom = otherTop + otherHeightPx

            //val otherTopCenter = Pair(pos.posX + otherWidthPx / 2, otherTop)
            val otherBottomCenter = Pair(otherBlock.X.value + otherWidthPx / 2, otherBottom)

            //val currentBottomCenter = Pair(currentX + blockWidth / 2, currentY + blockHeight)
            val currentTopCenter = Pair(currentBlock.X.value + currentBlock.getDynamicWidthPx(density) / 2, block.Y.value)

            val distanceCurrentTopToOtherBottom = distance(currentTopCenter, otherBottomCenter)
            //Log.println(Log.DEBUG, null, distanceCurrentTopToOtherBottom.toString())
            if (distanceCurrentTopToOtherBottom < 50f) {
                //Log.println(Log.DEBUG, null, listOf(currentX, currentY, pos.posX, otherBottom).toString())
                return Pair(otherBlock.X.value, otherBottom)
            }
            /*val distanceTopToBottom = distance(currentTopCenter, otherBottomCenter)
            if (distanceTopToBottom < 50f) {
                return Pair(pos.posX, otherBottom)
            }

            val distanceBottomToTop = distance(currentBottomCenter, otherTopCenter)
            if (distanceBottomToTop < 50f) {
                return Pair(pos.posX, otherBottom)
            }*/
        }

        return null
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(block.X.value.roundToInt(), animatedY.roundToInt()) }
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
                        val newX = (block.X.value + dragAmount.x).coerceAtLeast(0f)
                        val newY = (block.Y.value + dragAmount.y)

                        block.X.value = newX
                        block.Y.value = newY
                        val potentialSnap = findSnapTarget(block)
                        snapTarget = potentialSnap
                        isNearSnap = potentialSnap != null
                    },
                    onDragEnd = {
                        block.X.value = if (snapTarget != null) snapTarget!!.first else block.X.value
                        block.Y.value = if (snapTarget != null) snapTarget!!.second else block.Y.value

                        snapTarget = null
                        isNearSnap = false
                    }
                )
            }
    ) {
        BlockItem(
            block = block,
            onClick = { onShowDeleteChange(null) },
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
