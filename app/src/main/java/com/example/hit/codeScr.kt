package com.example.hit

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.hit.language.parser.IStatement

var font = FontFamily(Font(R.font.fredoka))

@Composable
fun CodeScreen(
    navController: NavHostController,
) {
    var showMenu by remember { mutableStateOf(false) }
    var showConsole by remember { mutableStateOf(false) }
    val listBlocks = remember { mutableStateListOf<CodeBlock>() }
    val blockPositions = remember { mutableStateMapOf<Int, BlockPosition>() }
    var blockId by remember { mutableStateOf<Int?>(null) }
    var nextBlockId by remember { mutableStateOf(1000) }
    val consoleOutput = remember { mutableStateListOf<String>() }






    val menuOff by animateDpAsState(
        targetValue = if (showMenu) 0.dp else (-300).dp,
    )

    val сonsoleOff by animateDpAsState(
        targetValue = if (showConsole) 0.dp else (360).dp,
    )

    val alpha by animateFloatAsState(
        targetValue = if (showMenu || showConsole) 0.5f else 0f,
    )

    val defaultBlocks = remember { BlockData.defaultBlocks }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        onClick = { showConsole = !showConsole},
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listBlocks.size, key = { listBlocks[it].id }) { index ->
                        val block = listBlocks[index]
                        val position = blockPositions[block.id] ?: BlockPosition(
                            id = block.id,
                            posX = 0f,
                            posY = 0f
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        ) {
                            Drag(
                                block = block,
                                position = position,
                                active = blockId == position.id,
                                initID = { blockId = position.id },
                                positionChange = { newPosition ->
                                    blockPositions[block.id] = newPosition },
                                allBlockPositions = blockPositions
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }

        if(showConsole) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha))
                    .clickable { showConsole = false }
            )
        }
        Box(
            modifier = Modifier
                .offset(y = сonsoleOff)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(360.dp)
                .navigationBarsPadding()
                .background(
                    color = Color(0xFFFFFFFF),
                    shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                )
        ){
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
                        onClick = { showConsole = false },
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
                        items(5) { index ->
                            Text(
                                text = "Console output line ${index + 1}",
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



        if(showMenu && !showConsole) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = alpha))
                    .clickable { showMenu = false }
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
                modifier = Modifier.padding(top = 52.dp, bottom = 52.dp, start = 12.dp, end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(defaultBlocks) { block ->
                    BlockItem(
                        showMenu = true,
                        block = block,
                        onClick = {
                            val newBlock = block.copy(id = nextBlockId)
                            listBlocks.add(newBlock)
                            blockPositions[newBlock.id] = BlockPosition(
                                id = newBlock.id,
                                posX = 0f,
                                posY = listBlocks.size * 60f
                            )
                            nextBlockId++
                        }
                    )
                }
            }
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockItem(
    showMenu : Boolean = false,
    block: CodeBlock,
    onClick: () -> Unit
) {
    var variableName by remember { mutableStateOf("") }
    var dataType by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var isDataTypeDropdownExpanded by remember { mutableStateOf(false) }

    val dataTypes = remember {
        listOf(
            "Int",
            "String",
            "Boolean",
            "Float",
            "Double",
            "Array"
        )
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Color.DarkGray,
        unfocusedTextColor = Color.DarkGray,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray,
        focusedIndicatorColor = Color.DarkGray,
        unfocusedIndicatorColor = Color.DarkGray.copy(alpha = 0.5f),
        focusedContainerColor = Color.White.copy(alpha = 0.9f),
        unfocusedContainerColor = Color.White.copy(alpha = 0.9f)
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(124.dp)
            .background(
                color = block.color,
                shape = PuzzleShape()
            )
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = block.type.value,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = font,
            )

            when (block.type) {
                BlockType.VARIABLE_INITIALIZATION, BlockType.VARIABLE_DECLARATION -> {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = variableName,
                            onValueChange = { variableName = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            singleLine = true,
                            enabled = !showMenu,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = font
                            )

                        )


                        Text(":", fontSize = 28.sp)

                        Box(modifier = Modifier.weight(1f)) {
                            ExposedDropdownMenuBox(
                                expanded = isDataTypeDropdownExpanded,
                                onExpandedChange = {if(!showMenu) isDataTypeDropdownExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = dataType,
                                    onValueChange = { },
                                    readOnly = true,
                                    enabled = !showMenu,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDataTypeDropdownExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = isDataTypeDropdownExpanded,
                                    onDismissRequest = { isDataTypeDropdownExpanded = false }
                                ) {
                                    dataTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type) },
                                            onClick = {
                                                dataType = type
                                                isDataTypeDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text(" = ", fontSize = 28.sp)

                        OutlinedTextField(
                            value = value,
                            onValueChange = { value = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            singleLine = true,
                            enabled = !showMenu,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = font
                            )
                        )
                    }
                }

                BlockType.IF, BlockType.ELSE_IF, BlockType.WHILE -> {
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !showMenu,
                        singleLine = true,
                        colors = textFieldColors
                    )
                }

                BlockType.FOR -> {
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        enabled = !showMenu,
                        singleLine = true,
                        colors = textFieldColors
                    )
                }

                else -> {
                    // Для остальных типов блоков показываем только название
                }
            }
        }
    }
}