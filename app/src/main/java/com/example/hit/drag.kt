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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.example.hit.R
import com.example.hit.blocks.BodyBlock
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Drag(
    block: BasicBlock,
    blocksOnScreen: MutableList<BasicBlock>,
    blockWithDeleteShownId: UUID?,
    onShowDeleteChange: (UUID?) -> Unit,
    onSwapMenu: (BodyBlock) -> Unit,
    onChangeSize: () -> Unit,
) {

    data class Snap(var topConnectedBlock: BasicBlock?, var bottomConnectedBlock: BasicBlock?)

    val GreyColor = colorResource(R.color.grey_001)

    val density = LocalDensity.current

    var isNearSnap by remember { mutableStateOf(false) }
    var snapTarget by remember { mutableStateOf<Snap>(Snap(null, null)) }

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

    var currentZIndex by remember { mutableFloatStateOf(1f) }

    fun distance(point1: Pair<Float, Float>, point2: Pair<Float, Float>): Float {
        val dx = point1.first - point2.first
        val dy = point1.second - point2.second
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    fun deleteBlock() {
        for (blockIndex in 0..blocksOnScreen.size - 1) {
            if (blocksOnScreen[blockIndex].id == block.id) {
                blocksOnScreen.removeAt(blockIndex)
                block.move()
                return
            }
        }
        Log.println(Log.ERROR, null, "Element with given id not found.")
    }


    fun findSnapTarget(
        currentBlock: BasicBlock
    ): Snap {
        var snap = Snap(null, null)
        for (otherBlock in blocksOnScreen) {
            if (otherBlock.id == currentBlock.id) continue

            val otherHeightPx = otherBlock.getDynamicHeightPx(density)
            val otherWidthPx = otherBlock.getDynamicWidthPx(density)
            val otherXStart = otherBlock.x
            val otherXEnd = otherBlock.x + otherWidthPx
            val otherTop = otherBlock.y
            val otherBottom = otherTop + otherHeightPx

            val thisHeightPx = block.getDynamicHeightPx(density)
            val thisWidthPx = block.getDynamicWidthPx(density)
            val thisXStart = block.x
            val thisXEnd = block.x + thisWidthPx
            val thisTop = block.y
            val thisBottom = thisTop + thisHeightPx


            val intersectionSize = min(otherXEnd, thisXEnd) - max(otherXStart, thisXStart)

            val distanceCurrentTopToOtherBottom = abs(thisTop - otherBottom)

            if (distanceCurrentTopToOtherBottom < 30f && intersectionSize > min(
                    thisWidthPx,
                    otherWidthPx
                ) * 0.7 && otherBlock.isBottomCompatible()
            ) {
                snap.topConnectedBlock = otherBlock
            }

            val distanceCurrentBottomToOtherTop = abs(thisBottom - otherTop)
            if (distanceCurrentBottomToOtherTop < 30f && intersectionSize > min(
                    thisWidthPx,
                    otherWidthPx
                ) * 0.7 && otherBlock.isTopCompatible()
            ) {
                snap.bottomConnectedBlock = otherBlock
            }
        }
        return snap
    }

    fun increaseZIndex() {
        var maxZIndex = 0f
        for (currentBlock in blocksOnScreen) {
            maxZIndex = max(maxZIndex, currentBlock.zIndex)
        }
        block.zIndex = maxZIndex + 1
        currentZIndex = block.zIndex
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
                        color = GreyColor,
                        size = size
                    )
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        onChangeSize()
                        increaseZIndex()
                        block.move()
                        snapTarget = Snap(null, null)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        if (block.parentBlock != null) {
                            currentX = min(
                                currentX + dragAmount.x,
                                block.parentBlock!!.getDynamicWidthPx(density) - block.getDynamicWidthPx(
                                    density
                                )
                            )
                            currentY = min(
                                currentY + dragAmount.y,
                                block.parentBlock!!.getDynamicHeightPx(density) - block.getDynamicHeightPx(
                                    density
                                )
                            )
                        } else {
                            currentX = currentX + dragAmount.x
                            currentY = currentY + dragAmount.y
                        }
                        currentX = currentX.coerceAtLeast(0f)
                        currentY = currentY.coerceAtLeast(0f)

                        block.x = currentX
                        block.y = currentY

                        val potentialSnap = findSnapTarget(block)
                        snapTarget = potentialSnap
                        isNearSnap =
                            (potentialSnap.topConnectedBlock != null || potentialSnap.bottomConnectedBlock != null)
                    },
                    onDragEnd = {
                        if (snapTarget.topConnectedBlock != null) {
                            val topBlock = snapTarget.topConnectedBlock!!
                            currentX = topBlock.x
                            currentY = topBlock.y + topBlock.getDynamicHeightPx(density)
                            block.x = currentX
                            block.y = currentY
                            block.connectTopBlock(topBlock)
                        }
                        if (snapTarget.bottomConnectedBlock != null) {
                            val bottomBlock = snapTarget.bottomConnectedBlock!!
                            currentX = bottomBlock.x
                            currentY = bottomBlock.y - block.getDynamicHeightPx(density)
                            block.x = currentX
                            block.y = currentY
                            block.connectBottomBlock(bottomBlock)
                        }
                        snapTarget = Snap(null, null)
                        isNearSnap = false
                    }
                )
            }
            .zIndex(currentZIndex)
    ) {
        BlockItem(
            block = block,
            onClick = { onShowDeleteChange(null) },
            onSwapMenu = onSwapMenu,
        )
        if (block.id == blockWithDeleteShownId) {
            Button(
                onClick = { deleteBlock() },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = font
                )
            }
        }
    }
}