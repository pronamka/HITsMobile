package com.example.hit

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntOffset
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.IOperation
import org.jetbrains.annotations.NonBlockingExecutor
import kotlin.math.roundToInt


var font = FontFamily(Font(R.font.fredoka))

@Composable
fun CodeScreen(
    navController: NavHostController,
) {
    var showMenu by remember { mutableStateOf(false) }
    var listBlocks = remember { mutableStateListOf<CodeBlock>() }
    var blockPositions = remember { mutableStateMapOf<Int, BlockPosition>() }
    var blockId by remember { mutableStateOf<Int?>(null) }

    fun runProgram(){
        val ourBlocks = listOf<BasicBlock>()
        val statements = mutableListOf<IStatement>()
        var i = 0
        while (i < ourBlocks.size){
            var block = ourBlocks[i]
            if (block is IfBlock){
                val blocks = mutableListOf<Pair<IOperation, BlockStatement>>()
                blocks.add(block.execute().blocks[0])
                if (i < ourBlocks.size-1) {
                    block = ourBlocks[++i]
                }
                while (block is ElifBlock){
                    blocks.add(block.execute().blocks[0])
                    if (i < ourBlocks.size-1) {
                        block = ourBlocks[++i]
                    } else {
                        break
                    }
                }
                var elseBlock: BlockStatement? = null
                if (block is ElseBlock){
                    elseBlock = block.execute()
                }
                statements.add(IfElseStatement(blocks, elseBlock))
                continue
            } else {
                i++
            }
            statements.add(block.execute())
        }
    }

    val menuOffset by animateDpAsState(
        targetValue = if (showMenu) 0.dp else (-300).dp,
        label = "menuAnimation"
    )
    val Alpha by animateFloatAsState(
        targetValue = if (showMenu) 0.5f else 0f,
        label = "overlayAnimation"
    )

    val defaultBlocks = remember { BlockData.defaultBlocks }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF7890D5))
                        .padding(26.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25813D)),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.padding(bottom = 24.dp).width(158.dp).height(58.dp)
                    ) {
                        Text(
                            "Launch code",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                            fontSize = 20.sp
                        )
                    }

                    Button(
                        onClick = {
                            listBlocks.clear()
                            blockPositions.clear() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA6294E)),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.padding(bottom = 24.dp).width(158.dp).height(58.dp)
                    ) {
                        Text(
                            "Clear",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = font,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        )
        { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF7890D5)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(72.dp)
                        .fillMaxWidth(0.9f)
                        .background(
                            color = Color(0xFF7943DE),
                            shape = RoundedCornerShape(56.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7943DE),
                            contentColor = Color.White
                        ),
                        onClick = { navController.navigate(Destinations.START_SCREEN) },
                    ) {
                        Icon(
                            Icons.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                    Text(
                        "Code Editor",
                        color = Color.White,
                        modifier = Modifier.padding(start = 16.dp),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font
                    )

                    Button(
                        onClick = { showMenu = !showMenu },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7943DE),
                            contentColor = Color.White
                        ),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Outlined.Add, contentDescription = null)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    listBlocks.forEach { block ->
                        val position = blockPositions[block.hashCode()] ?: BlockPosition(
                            id = block.hashCode(),
                            posX = 0f,
                            posY = 0f
                        )
                        Drag(
                            block,
                            position,
                            active = blockId == position.id,
                            { blockId = position.id },
                            { newPosition -> blockPositions[block.hashCode()] = newPosition }
                            /*{ newBlock  }*/
                        )
                    }
                }
            }
        }

        if(showMenu){
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = Alpha))
                    .clickable{showMenu = false}
            )
        }

        Box(
            modifier = Modifier
                .offset(x = menuOffset)
                .width(296.dp)
                .statusBarsPadding()
                .navigationBarsPadding()
                .fillMaxHeight()
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                )
        ) {
            LazyColumn(
                modifier = Modifier.padding(top = 52.dp, bottom = 52.dp, start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(defaultBlocks) { block ->
                    BlockItem(
                        block = block,
                        isFirst = false,
                        isLast = false,
                        onClick = {
                            listBlocks.add(block.copy())
                            val newBlock = block.copy()
                            blockPositions[newBlock.hashCode()] = BlockPosition(
                                id = newBlock.hashCode(),
                                posX = 0f,
                                posY = listBlocks.size * 150f
                            )
                        }
                    )
                }
            }
        }

    }
}

@Composable
fun BlockItem(
    block: CodeBlock,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical =  16.dp)
            .fillMaxWidth()
            .height(64.dp)
            .background(
                color = block.color,
                shape = PuzzleShape(isFirst, isLast)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = block.type.toString(),
            modifier = Modifier.padding(start = 16.dp),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = font
        )
    }
}
