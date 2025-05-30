import android.util.Log
import com.example.hit.BlockItem
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
import com.example.hit.blocks.BodyBlock
import com.example.hit.blocks.IfElseBlock

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Drag(
    block: BasicBlock,
    active : Boolean,
    initID : () -> Unit,
    blocksOnScreen: List<BasicBlock>,
    del: ()-> Unit,
    blockWithDeleteShownId: UUID?,
    onShowDeleteChange: (UUID?) -> Unit,
    onSwapMenu: (BodyBlock) -> Unit
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

    var currentX by remember { mutableStateOf(block.x) }
    var currentY by remember { mutableStateOf(block.y) }

    val animatedX by animateFloatAsState(
        targetValue = currentX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val animatedY by animateFloatAsState(
        targetValue = currentY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    fun distance(point1: Pair<Float, Float>, point2: Pair<Float, Float>): Float {
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
            val otherTop = otherBlock.y
            val otherBottom = otherTop + otherHeightPx

            val otherTopCenter = Pair(otherBlock.x + otherBlock.getDynamicWidthPx(density) / 2, otherBlock.y)
            val otherBottomCenter = Pair(otherBlock.x + otherWidthPx / 2, otherBottom)
            val currentTopCenter = Pair(currentBlock.x + currentBlock.getDynamicWidthPx(density) / 2, currentBlock.y)
            val currentBottomCenter = Pair(currentBlock.x + currentBlock.getDynamicWidthPx(density) / 2, currentBlock.y + currentBlock.getDynamicHeightPx(density))

            val distanceCurrentTopToOtherBottom = distance(currentTopCenter, otherBottomCenter)
            if (distanceCurrentTopToOtherBottom < 50f) {
                return Pair(otherBlock.x, otherBottom)
            }

            val distanceCurrentBottomToOtherTop = distance(currentBottomCenter, otherTopCenter)
            if (distanceCurrentBottomToOtherTop < 50f) {
                return Pair(otherBlock.x, otherTop - currentBlock.getDynamicHeightPx(density))
            }


            if (otherBlock is IfElseBlock) {

                val ifBlockTop = otherBlock.y
                val ifBlockBottom = ifBlockTop + otherBlock.getDynamicHeightPx(density)
                val ifBlockLeft = otherBlock.x
                val ifBlockRight = ifBlockLeft + otherBlock.getDynamicWidthPx(density)
                Log.println(Log.DEBUG, null, listOf(ifBlockTop, ifBlockBottom).toString())

                if (currentBlock.x > ifBlockLeft && currentBlock.x < ifBlockRight &&
                    currentBlock.y > ifBlockTop && currentBlock.y < ifBlockBottom) {
                    var closestBlock: BasicBlock? = null
                    var minDistance = Float.MAX_VALUE

                    for (innerBlock in otherBlock.blocksInput[0].second.blocks) {
                        val distance = distance(
                            Pair(currentBlock.x, currentBlock.y),
                            Pair(innerBlock.x, innerBlock.y)
                        )
                        if (distance < minDistance) {
                            minDistance = distance
                            closestBlock = innerBlock
                        }
                    }

                    if (closestBlock != null) {
                        return Pair(closestBlock.x, closestBlock.y)
                    }
                }
            }
        }
        return null
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(animatedX.roundToInt(), animatedY.roundToInt()) }
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
                        currentX = (currentX + dragAmount.x).coerceAtLeast(0f)
                        currentY = currentY + dragAmount.y

                        block.x = currentX
                        block.y = currentY

                        val potentialSnap = findSnapTarget(block)
                        snapTarget = potentialSnap
                        isNearSnap = potentialSnap != null
                    },
                    onDragEnd = {
                        if (snapTarget != null) {
                            currentX = snapTarget!!.first
                            currentY = snapTarget!!.second
                        }
                        block.x = currentX
                        block.y = currentY
                        snapTarget = null
                        isNearSnap = false
                    }
                )
            }
    ) {
        BlockItem(
            block = block,
            onClick = { onShowDeleteChange(null) },
            onSwapMenu = onSwapMenu
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