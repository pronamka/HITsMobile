import android.util.Log
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
) {

    data class Snap(var x: Float, var y: Float, var connectedBlock: BasicBlock, var isTop: Boolean)

    var lastInteractedTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
    val density = LocalDensity.current

    var isNearSnap by remember { mutableStateOf(false) }
    var snapTarget by remember { mutableStateOf<Snap?>(null) }

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
        //Log.println(Log.DEBUG, null, listOf(point1, point2).toString())
        val dx = point1.first - point2.first
        val dy = point1.second - point2.second
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }


    fun findSnapTarget(
        currentBlock: BasicBlock
    ): Snap? {
        for (otherBlock in blocksOnScreen) {
            if (otherBlock.id == currentBlock.id) continue

            val otherHeightPx = otherBlock.getDynamicHeightPx(density)
            val otherWidthPx = otherBlock.getDynamicWidthPx(density)
            val otherTop = otherBlock.y
            val otherBottom = otherTop + otherHeightPx

            val otherTopCenter =
                Pair(otherBlock.x + otherBlock.getDynamicWidthPx(density) / 2, otherBlock.y)

            val otherBottomCenter = Pair(otherBlock.x + otherWidthPx / 2, otherBottom)
            val currentTopCenter =
                Pair(currentBlock.x + currentBlock.getDynamicWidthPx(density) / 2, currentBlock.y)

            val currentBottomCenter = Pair(
                currentBlock.x + currentBlock.getDynamicWidthPx(density) / 2,
                currentBlock.y + currentBlock.getDynamicHeightPx(density)
            )

            //Log.println(Log.DEBUG, null, listOf(currentBlock.y, otherTop).toString())
            val distanceCurrentTopToOtherBottom = distance(currentTopCenter, otherBottomCenter)
            //Log.println(Log.DEBUG, null, otherBlock.bottomConnection.toString())
            if (distanceCurrentTopToOtherBottom < 50f && otherBlock.isBottomCompatible()) {
                return Snap(otherBlock.x, otherBottom, otherBlock, true) // block to to other bottom
            }
            val distanceCurrentBottomToOtherTop = distance(currentBottomCenter, otherTopCenter)
            if (distanceCurrentBottomToOtherTop < 50f && otherBlock.isTopCompatible()) {
                return Snap(
                    otherBlock.x,
                    otherTop - currentBlock.getDynamicHeightPx(density),
                    otherBlock,
                    false
                ) // block to to other top
            }
        }
        return null
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(animatedX.roundToInt(), animatedY.roundToInt()) }
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
                        block.move()
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
                            if (snapTarget!!.isTop) {
                                block.connectTopBlock(snapTarget!!.connectedBlock)
                            } else {
                                block.connectBottomBlock(snapTarget!!.connectedBlock)
                            }
                            currentX = snapTarget!!.x
                            currentY = snapTarget!!.y
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