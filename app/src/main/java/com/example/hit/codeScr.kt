package com.example.hit

import Drag
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.BlockData
import com.example.hit.blocks.BodyBlock
import com.example.hit.blocks.blockTypeToColor
import com.example.hit.blocks.container.Container
import com.example.hit.codeRunner.CodeRunner
import com.example.hit.console.console
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

var font = FontFamily(Font(R.font.fredoka))


@Composable
fun CodeScreen(
    navController: NavHostController,
) {
    var showDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showConsole by remember { mutableStateOf(false) }
    val listOfBlocks = remember { mutableStateListOf<BasicBlock>() }
    var blockId by remember { mutableStateOf<UUID?>(null) }
    val consoleOutput = remember { console }
    var menuForInnerBlock by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    val Scope = rememberCoroutineScope()
    var blockWithDeleteShownId by remember { mutableStateOf<UUID?>(null) }

    val menuOff by animateDpAsState(
        targetValue = if ((showMenu || menuForInnerBlock) ) 0.dp else (-300).dp,
    )

    val consoleOff by animateDpAsState(
        targetValue = if (!showMenu && showConsole && !menuForInnerBlock) 0.dp else (360).dp,
    )

    val alpha by animateFloatAsState(
        targetValue = if (showMenu || showConsole || menuForInnerBlock) 0.5f else 0f,
    )

    val defaultBlocks = remember { BlockData.defaultBlocks }

    var currentBodyBlock by remember { mutableStateOf<BodyBlock?>(null) }
    var temp = remember {mutableStateListOf<BasicBlock>()}

    fun changeMenuSetting(bodyBlock: BodyBlock){
        currentBodyBlock = bodyBlock
        temp.clear()
        temp.addAll(currentBodyBlock!!.blocks)
        menuForInnerBlock = true
        Log.println(Log.DEBUG, null, "$currentBodyBlock")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Error", fontFamily = font) },
                text = {
                    Text(
                        "Invalid block position",
                        fontFamily = font
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK", fontFamily = font)
                    }
                }
            )
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF7890D5))
                        .padding(26.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            val container = Container(listOfBlocks)
                            if (container.isValidBlockArrangement()) {
                                val codeRunner = CodeRunner(container, consoleOutput)
                                codeRunner.run()
                                showConsole = !showConsole
                            } else {
                                showDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25813D)),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .width(158.dp)
                            .height(58.dp)
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
                            listOfBlocks.clear()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA6294E)),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .width(158.dp)
                            .height(58.dp)
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
        ) { padding ->
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
                        .verticalScroll(scrollState)
                        .clickable { blockWithDeleteShownId = null },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(2000.dp)
                    ) {
                        listOfBlocks.forEach { block ->
                            key(block.id){
                                Drag(
                                    block = block,
                                    blocksOnScreen = listOfBlocks,
                                    blockWithDeleteShownId = blockWithDeleteShownId,
                                    onShowDeleteChange = { id -> blockWithDeleteShownId = id },
                                    onSwapMenu = { bodyBlock -> changeMenuSetting(bodyBlock)}
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(150.dp))
                    }
                }

            }
        }

        if (showConsole) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha))
                    .clickable { showConsole = false }
            )
        }
        Box(
            modifier = Modifier
                .offset(y = consoleOff)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(360.dp)
                .navigationBarsPadding()
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Console Output",
                        color = Color(0xFF7943DE),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = font
                    )

                    Button(
                        onClick = { showConsole = false;
                                  for (block in listOfBlocks) {
                                      block.color.value = blockTypeToColor[block.type]!!
                                  }},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA6294E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Close", fontFamily = font)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            color = Color.LightGray.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(consoleOutput.size) { index ->
                            Text(
                                text = consoleOutput[index],
                                color = Color.DarkGray,
                                fontFamily = font,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }


        if(showMenu || menuForInnerBlock) {
            Box(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = alpha))
                    .fillMaxSize()
                    .clickable {
                        if (showMenu) showMenu = false
                        if (menuForInnerBlock) menuForInnerBlock = false
                    }
            )
        }



        Box(
            modifier = Modifier
                .offset(x = menuOff)
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
                modifier = Modifier.padding(
                    top = 52.dp,
                    bottom = 52.dp,
                    start = 12.dp,
                    end = 12.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(defaultBlocks) { block ->
                    BlockItem(
                        showMenu = true,
                        block = block,
                        onClick = {
                            val newBlock = block.deepCopy()
                            if (showMenu) listOfBlocks.add(newBlock)
                            else {
                                if (currentBodyBlock==null){
                                    listOfBlocks.add(newBlock)
                                }
                                currentBodyBlock!!.addBlock(newBlock)
                                temp.add(newBlock)
                            }
                            Scope.launch {
                                delay(100)
                                lazyListState.animateScrollToItem(listOfBlocks.size - 1)
                            }
                        },
                        onSwapMenu = { bodyBlock -> changeMenuSetting(bodyBlock)}
                    )
                }
            }
        }
    }
}