package com.example.hit

import Drag
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hit.blocks.AssignmentBlock
import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.BodyBlock
import com.example.hit.blocks.BreakBlock
import com.example.hit.blocks.ContinueBlock
import com.example.hit.blocks.DeclarationBlock
import com.example.hit.blocks.ForBlock
import com.example.hit.blocks.IfElseBlock
import com.example.hit.blocks.InitializationBlock
import com.example.hit.blocks.PrintBlock
import com.example.hit.blocks.ReturnBlock
import com.example.hit.blocks.WhileBlock
import com.example.hit.language.parser.exceptions.ReturnException
import java.util.UUID


fun onChange(block: BasicBlock, density: Density) {
    if (block is IfElseBlock || block is WhileBlock || block is ForBlock) {
        block.heightDP = with(density) { block.getDynamicHeightPx(density).toDp() }
        block.widthDP = with(density) { block.getDynamicWidthPx(density).toDp() }
    }
}

object NumberConstants {

    val bodyBlockHorizontalPadding = 8.dp
    val bodyBlockVerticalPadding = 12.dp

    val standardColumnPadding = 8.dp
    val standardColumnHorizontalArrangement = 8.dp
    val standardColumnVerticalArrangement = 8.dp

    val standardRowHorizontalArrangement = 8.dp

    val standardBlockWidth = 256.dp
    val standardBlockHeight = 80.dp

    val wideBlockWidth = 350.dp
    val wideBlockHeight = 250.dp

    val standardBoxPadding = 16.dp

    val standardInputFieldHeight = 56.dp
    val standardInputFieldWidth = 220.dp

    val standardSpacerHeight = 10.dp

    val standardAddElseBlockButtonHeight = 48.dp
    val standardAddElseBlockButtonWidth = 104.dp

    val standardIfLabelWidth = 60.dp

    val addBlockButtonWidth = 60.dp

    val rowWidth = standardIfLabelWidth + standardInputFieldWidth + addBlockButtonWidth

    val borderWidth = 2.dp


    val roundCornerShape = 16.dp


    val bodyBlockTransparencyIndex = 0.2f
    val borderTransparencyIndex = 0.5f
}


@Composable
fun BlockItem(
    showMenu: Boolean = false,
    block: BasicBlock,
    onClick: () -> Unit,
    onSwapMenu: (BodyBlock) -> Unit,
) {
    var blockInnerId by remember { mutableStateOf<UUID?>(null) }
    val density = LocalDensity.current
    var innerBlockWithDeleteShownId by remember { mutableStateOf<UUID?>(null) }


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

    when (block) {
        is InitializationBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = { block.nameInput.set(it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp, fontFamily = font
                        )
                    )
                    Text(
                        text = stringResource(R.string.simbol1),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.typeInput.getInputField(),
                        onValueChange = { block.typeInput.set(it) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp, fontFamily = font
                        )
                    )

                    Text(
                        text = stringResource(R.string.simbol2),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = {
                            block.valueInput.set(it)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp, fontFamily = font
                        )
                    )
                }
            }
        }

        is IfElseBlock -> {
            var hasElse by remember { mutableStateOf(false) }
            var ifCondition by remember { mutableStateOf("") }
            var elseIfCounts by remember { mutableStateOf(listOf<String>()) }

            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.standardBoxPadding,
                        vertical = NumberConstants.standardBoxPadding
                    )
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    //.wrapContentWidth()
                    //.wrapContentHeight()
                    .defaultMinSize(
                        minHeight = NumberConstants.wideBlockHeight,
                        minWidth = NumberConstants.wideBlockWidth
                    )
                    .height(block.getDynamicHeightDp(density))
                    .width(block.getDynamicWidthDp(density))
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(NumberConstants.standardColumnPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(NumberConstants.standardColumnHorizontalArrangement),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.standardIfLabelWidth)
                        )
                        OutlinedTextField(
                            value = ifCondition,
                            onValueChange = {
                                ifCondition = it
                                block.setNewCondition(ifCondition, 0)
                            },
                            modifier = Modifier
                                .width(NumberConstants.standardInputFieldWidth)
                                .height(NumberConstants.standardInputFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors
                        )
                        Button(
                            onClick = {
                                if (!showMenu) {
                                    onSwapMenu(block.blocksInput[0].second)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7943DE), contentColor = Color.White
                            ),
                            modifier = Modifier.width(NumberConstants.addBlockButtonWidth)
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
                            .padding(
                                horizontal = NumberConstants.bodyBlockHorizontalPadding,
                                vertical = NumberConstants.bodyBlockVerticalPadding
                            )
                            .defaultMinSize(
                                minHeight = NumberConstants.standardBlockHeight,
                                minWidth = NumberConstants.standardBlockWidth
                            )
                            .height(block.blocksInput[0].second.getDynamicHeightDp(density))
                            .width(block.blocksInput[0].second.getDynamicWidthDp(density))
                            .clickable { innerBlockWithDeleteShownId = null }
                            .background(
                                color = Color.White.copy(alpha = NumberConstants.bodyBlockTransparencyIndex),
                                shape = RoundedCornerShape(NumberConstants.roundCornerShape)
                            )
                            .border(
                                width = NumberConstants.borderWidth,
                                color = Color.White.copy(alpha = NumberConstants.borderTransparencyIndex),
                                shape = RoundedCornerShape(NumberConstants.roundCornerShape)
                            )) {
                        block.blocksInput[0].second.blocks.forEach { blockInner ->
                            key(blockInner.id) {
                                Drag(
                                    block = blockInner,
                                    blocksOnScreen = block.blocksInput[0].second.blocks,
                                    blockWithDeleteShownId = innerBlockWithDeleteShownId,
                                    onShowDeleteChange = { id -> innerBlockWithDeleteShownId = id },
                                    onSwapMenu = onSwapMenu,
                                    onChangeSize = { onChange(blockInner, density) })
                            }

                        }
                        Spacer(modifier = Modifier.height(NumberConstants.standardSpacerHeight))
                    }

                    elseIfCounts.forEachIndexed { index, condition ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(NumberConstants.standardColumnVerticalArrangement)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(NumberConstants.standardRowHorizontalArrangement),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.else_if),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = font,
                                    modifier = Modifier.width(NumberConstants.standardIfLabelWidth)
                                )
                                OutlinedTextField(
                                    value = condition,
                                    onValueChange = { newCondition ->
                                        elseIfCounts = elseIfCounts.toMutableList().also {
                                            it[index] = newCondition
                                        }
                                        block.setNewCondition(elseIfCounts[index], index + 1)
                                    },
                                    modifier = Modifier
                                        .width(NumberConstants.standardInputFieldWidth)
                                        .height(NumberConstants.standardInputFieldHeight),
                                    enabled = !showMenu,
                                    singleLine = true,
                                    colors = textFieldColors
                                )
                                Button(
                                    onClick = {
                                        if (!showMenu) {
                                            onSwapMenu(block.blocksInput[index + 1].second)
                                        }
                                    },
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
                                    .padding(
                                        horizontal = NumberConstants.bodyBlockHorizontalPadding,
                                        vertical = NumberConstants.bodyBlockVerticalPadding
                                    )
                                    .defaultMinSize(
                                        minHeight = NumberConstants.standardBlockHeight,
                                        minWidth = NumberConstants.standardBlockWidth
                                    )
                                    .height(
                                        block.blocksInput[index + 1].second.getDynamicHeightDp(
                                            density
                                        )
                                    )
                                    .width(
                                        block.blocksInput[index + 1].second.getDynamicWidthDp(
                                            density
                                        )
                                    )
                                    .clickable { innerBlockWithDeleteShownId = null }
                                    .background(
                                        color = Color.White.copy(alpha = NumberConstants.bodyBlockTransparencyIndex),
                                        shape = RoundedCornerShape(NumberConstants.roundCornerShape)
                                    )
                                    .border(
                                        width = NumberConstants.borderWidth,
                                        color = Color.White.copy(alpha = NumberConstants.borderTransparencyIndex),
                                        shape = RoundedCornerShape(NumberConstants.roundCornerShape)
                                    )) {
                                block.blocksInput[index + 1].second.blocks.forEach { blockInner ->
                                    Drag(
                                        block = blockInner,
                                        blocksOnScreen = block.blocksInput[index + 1].second.blocks,
                                        blockWithDeleteShownId = innerBlockWithDeleteShownId,
                                        onShowDeleteChange = { id ->
                                            innerBlockWithDeleteShownId = id
                                        },
                                        onSwapMenu = onSwapMenu,
                                        onChangeSize = { onChange(blockInner, density) })
                                }
                                Spacer(modifier = Modifier.height(NumberConstants.standardSpacerHeight))
                            }
                        }
                    }

                    if (hasElse) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(unbounded = true)
                                .defaultMinSize(
                                    minHeight = NumberConstants.standardBlockHeight,
                                    minWidth = NumberConstants.standardBlockWidth
                                ),
                            verticalArrangement = Arrangement.spacedBy(NumberConstants.standardColumnVerticalArrangement)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.els),
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = font,
                                    modifier = Modifier.width(NumberConstants.standardIfLabelWidth)
                                )

                                Button(
                                    onClick = {
                                        if (!showMenu) {
                                            onSwapMenu(block.defaultBlockInput!!)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF7943DE),
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.width(NumberConstants.addBlockButtonWidth)
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
                                    .padding(
                                        horizontal = NumberConstants.bodyBlockHorizontalPadding,
                                        vertical = NumberConstants.bodyBlockVerticalPadding
                                    )
                                    .defaultMinSize(
                                        minHeight = NumberConstants.standardBlockHeight,
                                        minWidth = NumberConstants.standardBlockWidth
                                    )
                                    .height(block.defaultBlockInput!!.getDynamicHeightDp(density))
                                    .width(block.defaultBlockInput!!.getDynamicWidthDp(density))
                                    .clickable { innerBlockWithDeleteShownId = null }
                                    .background(
                                        color = Color.White.copy(alpha = NumberConstants.bodyBlockTransparencyIndex),
                                        shape = RoundedCornerShape(NumberConstants.roundCornerShape)
                                    )
                                    .border(
                                        width = NumberConstants.borderWidth,
                                        color = Color.White.copy(alpha = NumberConstants.borderTransparencyIndex),
                                        shape = RoundedCornerShape(NumberConstants.roundCornerShape)
                                    )) {
                                block.defaultBlockInput!!.blocks.forEach { blockFor ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Drag(
                                            block = blockFor,
                                            blocksOnScreen = block.defaultBlockInput!!.blocks,
                                            blockWithDeleteShownId = innerBlockWithDeleteShownId,
                                            onShowDeleteChange = { id ->
                                                innerBlockWithDeleteShownId = id
                                            },
                                            onSwapMenu = onSwapMenu,
                                            onChangeSize = { onChange(blockFor, density) })
                                    }
                                }
                                Spacer(modifier = Modifier.height(NumberConstants.standardSpacerHeight))
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier, horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier
                                    .width(NumberConstants.standardAddElseBlockButtonWidth)
                                    .height(NumberConstants.standardAddElseBlockButtonHeight),
                                onClick = {
                                    elseIfCounts = elseIfCounts + "";
                                    block.addElseIfBlock("")
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF7943DE
                                    )
                                )
                            ) {
                                Text(
                                    stringResource(R.string.else_if),
                                    color = Color.White,
                                    fontFamily = font,
                                    fontSize = 18.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                modifier = Modifier
                                    .width(NumberConstants.standardAddElseBlockButtonWidth)
                                    .height(NumberConstants.standardAddElseBlockButtonHeight),
                                onClick = {
                                    hasElse = true;
                                    block.addElseBlock()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7943DE)
                                )
                            ) {
                                Text(
                                    stringResource(R.string.els),
                                    color = Color.White,
                                    fontFamily = font,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        is DeclarationBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = {
                            block.nameInput.set(it);
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp, fontFamily = font
                        )
                    )
                    Text(
                        text = stringResource(R.string.simbol1),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )
                    OutlinedTextField(
                        value = block.typeInput.getInputField(),
                        onValueChange = {
                            block.typeInput.set(it);
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp, fontFamily = font
                        )
                    )
                }
            }
        }

        is AssignmentBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.get(),
                        onValueChange = {
                            block.nameInput.set(it);
                        },
                        modifier = Modifier
                            .width(84.dp)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp, fontFamily = font
                        )

                    )

                    Text(
                        text = stringResource(R.string.simbol2),
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = { block.valueInput.set(it) },
                        modifier = Modifier
                            .width(84.dp)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp, fontFamily = font
                        )

                    )

                }
            }
        }

        is ForBlock -> {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        Text(
                            text = stringResource(R.string.simbol3),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.initializerInput.getInputField(),
                            onValueChange = { block.initializerInput.set(it) },
                            modifier = Modifier
                                .width(72.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp, fontFamily = font
                            )
                        )

                        Text(
                            text = stringResource(R.string.simbol5),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.conditionInput.getInputField(),
                            onValueChange = { block.conditionInput.set(it) },
                            modifier = Modifier
                                .width(72.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp, fontFamily = font
                            )
                        )

                        Text(
                            text = stringResource(R.string.simbol5),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.stateChangeInput.getInputField(),
                            onValueChange = { block.stateChangeInput.set(it) },
                            modifier = Modifier
                                .width(72.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp, fontFamily = font
                            )
                        )

                        Text(
                            text = stringResource(R.string.simbol4),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )
                    }

                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .wrapContentHeight(unbounded = true)
                            .defaultMinSize(minHeight = 60.dp, minWidth = 252.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }
            }
        }

        is WhileBlock -> {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        Text(
                            text = stringResource(R.string.simbol3),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.conditionInput.getInputField(),
                            onValueChange = { block.conditionInput.set(it) },
                            modifier = Modifier
                                .width(146.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 16.sp, fontFamily = font
                            )
                        )

                        Text(
                            text = stringResource(R.string.simbol4),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )
                    }

                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .wrapContentHeight(unbounded = true)
                            .defaultMinSize(minHeight = 60.dp, minWidth = 252.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 2.dp,
                                color = Color.White.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                    )
                }
            }
        }

        is ReturnBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.valueInputField.getInputField(),
                            onValueChange = { block.valueInputField.set(it) },
                            modifier = Modifier
                                .width(126.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 16.sp, fontFamily = font
                            )
                        )
                    }
                }
            }
        }

        is ContinueBlock, is BreakBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = block.type.value,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )
                }
            }
        }

        is PrintBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value, shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        Text(
                            text = stringResource(R.string.simbol3),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.valueInput.getInputField(),
                            onValueChange = { block.valueInput.set(it) },
                            modifier = Modifier
                                .width(126.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 16.sp, fontFamily = font
                            )
                        )

                        Text(
                            text = ")",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )
                    }
                }
            }
        }
    }
}