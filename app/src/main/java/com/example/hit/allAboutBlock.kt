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
import com.example.hit.blocks.FunctionBlock
import com.example.hit.blocks.IfElseBlock
import com.example.hit.blocks.InitializationBlock
import com.example.hit.blocks.PrintBlock
import com.example.hit.blocks.ReturnBlock
import com.example.hit.blocks.WhileBlock
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

    val columnPadding = 8.dp
    val boxPadding = 16.dp
    val boxRoundedShape = 24.dp

    val columnHorizontalArrangement = 8.dp
    val columnVerticalArrangement = 8.dp

    val rowHorizontalArrangement = 8.dp
    val rowHorizontalPadding = 16.dp
    val rowVerticalPadding = 8.dp

    val standardBlockWidth = 256.dp
    val standardBlockHeight = 80.dp

    val wideBlockWidth = 350.dp
    val wideBlockHeight = 250.dp

    val spacerHeight = 10.dp
    val spacerWidth = 12.dp

    val addBlockButtonWidth = 60.dp

    val borderWidth = 2.dp

    val roundCornerShape = 16.dp

    val bodyBlockTransparencyIndex = 0.2f
    val borderTransparencyIndex = 0.5f
    val singleSymbolWidth = 4.dp

    val inputTextFieldHeight = 56.dp
    val inputTextFieldWidth = 84.dp
    val inputFieldStandardWeight = 1f

    val inputFieldFontSize = 18.sp
    val textFontSize = 26.sp

    object IfElseBlock {

        val standardIfLabelWidth = 60.dp

        val standardInputFieldHeight = inputTextFieldHeight
        val standardInputFieldWidth = 190.dp

        val rowWidth = standardIfLabelWidth + standardInputFieldWidth + addBlockButtonWidth
        val overallPadding = columnPadding * 2 + boxPadding * 2

        val standardAddElseBlockButtonHeight = 48.dp
        val standardAddElseBlockButtonWidth = 104.dp
    }


    object ForBlock {
        val horizontalArrangement = 8.dp

        val inputTextFieldWidth = 72.dp
        val inputTextFieldHeight = inputTextFieldWidth

        val labelWidth = 50.dp

        val rowWidth =
            labelWidth + inputTextFieldWidth * 3 + singleSymbolWidth * 4 + horizontalArrangement * 9 + addBlockButtonWidth
        val overallPadding = columnPadding * 2 + boxPadding * 2
    }

    object WhileBlock {
        val horizontalArrangement = 8.dp

        val inputTextFieldWidth = 146.dp
        val inputTextFieldHeight = inputTextFieldWidth

        val labelWidth = 70.dp

        val rowPadding = 16.dp

        val rowWidth =
            labelWidth + inputTextFieldWidth + singleSymbolWidth * 2 + horizontalArrangement * 6 + addBlockButtonWidth
        val overallPadding = columnPadding * 2 + boxPadding * 2
    }

    object Function {
        val horizontalArrangement = 8.dp

        val inputTextFieldWidth = 52.dp
        val inputTextFieldHeight = 56.dp

        val labelWidth = 50.dp


        val parametersInputTextFieldWidth = 112.dp

        val rowWidth =
            labelWidth + inputTextFieldWidth * 2 + parametersInputTextFieldWidth + horizontalArrangement * 4 + addBlockButtonWidth
        val overallPadding = columnPadding * 2 + boxPadding * 2
    }

    object ReturnBlock{
        val inputFieldWidth = 126.dp
    }

    object OtherConstants {
        val halfTransparent = 0.5f
        val almostTransparent = 0.9f

    }
}


@Composable
fun BlockItem(
    showMenu: Boolean = false,
    block: BasicBlock,
    onClick: () -> Unit,
    onSwapMenu: (BodyBlock) -> Unit,
) {
    val density = LocalDensity.current
    var innerBlockWithDeleteShownId by remember { mutableStateOf<UUID?>(null) }


    val textFieldColors = TextFieldDefaults.colors(
        focusedTextColor = Color.DarkGray,
        unfocusedTextColor = Color.DarkGray,
        focusedPlaceholderColor = Color.Gray,
        unfocusedPlaceholderColor = Color.Gray,
        focusedIndicatorColor = Color.DarkGray,
        unfocusedIndicatorColor = Color.DarkGray.copy(alpha = NumberConstants.OtherConstants.halfTransparent),
        focusedContainerColor = Color.White.copy(alpha = NumberConstants.OtherConstants.almostTransparent),
        unfocusedContainerColor = Color.White.copy(alpha = NumberConstants.OtherConstants.almostTransparent)
    )

    when (block) {
        is InitializationBlock -> {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .width(NumberConstants.standardBlockWidth)
                    .height(NumberConstants.standardBlockHeight)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = NumberConstants.rowHorizontalArrangement),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = { block.nameInput.set(it) },
                        modifier = Modifier
                            .weight(NumberConstants.inputFieldStandardWeight)
                            .height(NumberConstants.inputTextFieldHeight),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )
                    )
                    Text(
                        text = stringResource(R.string.simbol1),
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.typeInput.getInputField(),
                        onValueChange = { block.typeInput.set(it) },
                        modifier = Modifier
                            .weight(NumberConstants.inputFieldStandardWeight)
                            .height(NumberConstants.inputTextFieldHeight),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )
                    )

                    Text(
                        text = stringResource(R.string.simbol2),
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = {
                            block.valueInput.set(it)
                        },
                        modifier = Modifier
                            .weight(NumberConstants.inputFieldStandardWeight)
                            .height(NumberConstants.inputTextFieldHeight),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
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
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .defaultMinSize(
                        minHeight = NumberConstants.wideBlockHeight,
                        minWidth = NumberConstants.wideBlockWidth
                    )
                    .height(block.getDynamicHeightDp(density))
                    .width(block.getDynamicWidthDp(density))
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(NumberConstants.columnPadding),
                    verticalArrangement = Arrangement.spacedBy(NumberConstants.columnVerticalArrangement)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(NumberConstants.columnHorizontalArrangement),
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.IfElseBlock.standardIfLabelWidth)
                        )
                        OutlinedTextField(
                            value = ifCondition,
                            onValueChange = {
                                ifCondition = it
                                block.setNewCondition(ifCondition, 0)
                            },
                            modifier = Modifier
                                .width(NumberConstants.IfElseBlock.standardInputFieldWidth)
                                .height(NumberConstants.IfElseBlock.standardInputFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors
                        )
                        Button(
                            onClick = {
                                if (!showMenu) {
                                    onSwapMenu(block.blocksInput[0].second)
                                }
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7943DE), contentColor = Color.White
                            ), modifier = Modifier.width(NumberConstants.addBlockButtonWidth)
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
                        Spacer(modifier = Modifier.height(NumberConstants.spacerHeight))
                    }

                    elseIfCounts.forEachIndexed { index, condition ->
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(NumberConstants.columnVerticalArrangement)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(NumberConstants.rowHorizontalArrangement),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.else_if),
                                    color = Color.White,
                                    fontSize = NumberConstants.textFontSize,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = font,
                                    modifier = Modifier.width(NumberConstants.IfElseBlock.standardIfLabelWidth)
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
                                        .width(NumberConstants.IfElseBlock.standardInputFieldWidth)
                                        .height(NumberConstants.IfElseBlock.standardInputFieldHeight),
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
                                    key(blockInner.id) {
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

                                }
                                Spacer(modifier = Modifier.height(NumberConstants.spacerHeight))
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
                            verticalArrangement = Arrangement.spacedBy(NumberConstants.columnVerticalArrangement)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.els),
                                    color = Color.White,
                                    fontSize = NumberConstants.textFontSize,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = font,
                                    modifier = Modifier.width(NumberConstants.IfElseBlock.standardIfLabelWidth)
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
                                    key(blockFor.id) {
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
                                Spacer(modifier = Modifier.height(NumberConstants.spacerHeight))
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier, horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier
                                    .width(NumberConstants.IfElseBlock.standardAddElseBlockButtonWidth)
                                    .height(NumberConstants.IfElseBlock.standardAddElseBlockButtonHeight),
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
                                    fontSize = NumberConstants.inputFieldFontSize
                                )
                            }
                            Spacer(modifier = Modifier.width(NumberConstants.spacerWidth))
                            Button(
                                modifier = Modifier
                                    .width(NumberConstants.IfElseBlock.standardAddElseBlockButtonWidth)
                                    .height(NumberConstants.IfElseBlock.standardAddElseBlockButtonHeight),
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
                                    fontSize = NumberConstants.inputFieldFontSize
                                )
                            }
                        }
                    }
                }
            }
        }

        is DeclarationBlock -> {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .width(NumberConstants.standardBlockWidth)
                    .height(NumberConstants.standardBlockHeight)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = NumberConstants.rowHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(NumberConstants.rowHorizontalArrangement),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = {
                            block.nameInput.set(it);
                        },
                        modifier = Modifier
                            .weight(NumberConstants.inputFieldStandardWeight)
                            .height(NumberConstants.inputTextFieldHeight),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )
                    )
                    Text(
                        text = stringResource(R.string.simbol1),
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )
                    OutlinedTextField(
                        value = block.typeInput.getInputField(),
                        onValueChange = {
                            block.typeInput.set(it);
                        },
                        modifier = Modifier
                            .weight(NumberConstants.inputFieldStandardWeight)
                            .height(NumberConstants.inputTextFieldHeight),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )
                    )
                }
            }
        }

        is AssignmentBlock -> {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .width(NumberConstants.standardBlockWidth)
                    .height(NumberConstants.standardBlockHeight)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = NumberConstants.rowHorizontalPadding,
                        vertical = NumberConstants.rowVerticalPadding
                    ),
                    horizontalArrangement = Arrangement.spacedBy(NumberConstants.rowHorizontalArrangement),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.get(),
                        onValueChange = {
                            block.nameInput.set(it);
                        },
                        modifier = Modifier
                            .width(NumberConstants.inputTextFieldWidth)
                            .height(NumberConstants.inputTextFieldHeight),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )

                    )

                    Text(
                        text = stringResource(R.string.simbol2),
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = { block.valueInput.set(it) },
                        modifier = Modifier
                            .width(NumberConstants.inputTextFieldWidth)
                            .height(NumberConstants.inputTextFieldHeight),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )

                    )

                }
            }
        }

        is ForBlock -> {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .defaultMinSize(
                        minHeight = NumberConstants.wideBlockHeight,
                        minWidth = NumberConstants.wideBlockWidth
                    )
                    .height(block.getDynamicHeightDp(density))
                    .width(block.getDynamicWidthDp(density))
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(NumberConstants.columnPadding),
                    verticalArrangement = Arrangement.spacedBy(NumberConstants.columnVerticalArrangement)
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(NumberConstants.ForBlock.horizontalArrangement),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.ForBlock.labelWidth)
                        )

                        Text(
                            text = stringResource(R.string.simbol3),
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.singleSymbolWidth)
                        )

                        OutlinedTextField(
                            value = block.initializerInput.getInputField(),
                            onValueChange = { block.initializerInput.set(it) },
                            modifier = Modifier
                                .width(NumberConstants.ForBlock.inputTextFieldWidth)
                                .height(NumberConstants.ForBlock.inputTextFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                            )
                        )

                        Text(
                            text = stringResource(R.string.simbol5),
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.singleSymbolWidth)
                        )

                        OutlinedTextField(
                            value = block.conditionInput.getInputField(),
                            onValueChange = { block.conditionInput.set(it) },
                            modifier = Modifier
                                .width(NumberConstants.ForBlock.inputTextFieldWidth)
                                .height(NumberConstants.ForBlock.inputTextFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                            )
                        )

                        Text(
                            text = stringResource(R.string.simbol5),
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.singleSymbolWidth)
                        )

                        OutlinedTextField(
                            value = block.stateChangeInput.getInputField(),
                            onValueChange = { block.stateChangeInput.set(it) },
                            modifier = Modifier
                                .width(NumberConstants.ForBlock.inputTextFieldWidth)
                                .height(NumberConstants.ForBlock.inputTextFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                            )
                        )

                        Text(
                            text = stringResource(R.string.simbol4),
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.singleSymbolWidth)
                        )

                        Button(
                            onClick = {
                                if (!showMenu) {
                                    onSwapMenu(block.blocks)
                                }
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7943DE), contentColor = Color.White
                            ), modifier = Modifier.width(NumberConstants.addBlockButtonWidth)
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
                            .height(block.blocks.getDynamicHeightDp(density))
                            .width(block.blocks.getDynamicWidthDp(density))
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
                        block.blocks.blocks.forEach { blockInner ->
                            key(blockInner.id) {
                                Drag(
                                    block = blockInner,
                                    blocksOnScreen = block.blocks.blocks,
                                    blockWithDeleteShownId = innerBlockWithDeleteShownId,
                                    onShowDeleteChange = { id -> innerBlockWithDeleteShownId = id },
                                    onSwapMenu = onSwapMenu,
                                    onChangeSize = { onChange(blockInner, density) })
                            }

                        }
                        Spacer(modifier = Modifier.height(NumberConstants.spacerHeight))
                    }
                }
            }
        }

        is WhileBlock -> {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.WhileBlock.rowPadding,
                        vertical = NumberConstants.WhileBlock.rowPadding
                    )
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .defaultMinSize(
                        minHeight = NumberConstants.wideBlockHeight,
                        minWidth = NumberConstants.wideBlockWidth
                    )
                    .height(block.getDynamicHeightDp(density))
                    .width(block.getDynamicWidthDp(density))
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(NumberConstants.columnPadding),
                    verticalArrangement = Arrangement.spacedBy(NumberConstants.columnVerticalArrangement)
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(NumberConstants.WhileBlock.horizontalArrangement),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.WhileBlock.labelWidth)
                        )

                        Text(
                            text = stringResource(R.string.simbol3),
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.singleSymbolWidth)
                        )

                        OutlinedTextField(
                            value = block.conditionInput.getInputField(),
                            onValueChange = { block.conditionInput.set(it) },
                            modifier = Modifier
                                .width(NumberConstants.WhileBlock.inputTextFieldWidth)
                                .height(NumberConstants.WhileBlock.inputTextFieldHeight),
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
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.singleSymbolWidth)
                        )

                        Button(
                            onClick = {
                                if (!showMenu) {
                                    onSwapMenu(block.blocks)
                                }
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF7943DE), contentColor = Color.White
                            ), modifier = Modifier.width(NumberConstants.addBlockButtonWidth)
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
                            .height(block.blocks.getDynamicHeightDp(density))
                            .width(block.blocks.getDynamicWidthDp(density))
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
                        block.blocks.blocks.forEach { blockInner ->
                            key(blockInner.id) {
                                Drag(
                                    block = blockInner,
                                    blocksOnScreen = block.blocks.blocks,
                                    blockWithDeleteShownId = innerBlockWithDeleteShownId,
                                    onShowDeleteChange = { id -> innerBlockWithDeleteShownId = id },
                                    onSwapMenu = onSwapMenu,
                                    onChangeSize = { onChange(blockInner, density) })
                            }

                        }
                        Spacer(modifier = Modifier.height(NumberConstants.spacerHeight))
                    }
                }
            }
        }

        is ReturnBlock -> {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .width(NumberConstants.standardBlockWidth)
                    .height(NumberConstants.standardBlockHeight)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = NumberConstants.rowHorizontalPadding,
                        vertical = NumberConstants.rowVerticalPadding
                    ),
                    horizontalArrangement = Arrangement.spacedBy(NumberConstants.rowHorizontalArrangement/2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = block.type.value,
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInputField.getInputField(),
                        onValueChange = { block.valueInputField.set(it) },
                        modifier = Modifier
                            .width(NumberConstants.ReturnBlock.inputFieldWidth)
                            .height(NumberConstants.inputTextFieldHeight),
                        enabled = !showMenu,
                        singleLine = true,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )
                    )
                }
            }
        }

        is ContinueBlock, is BreakBlock -> {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .width(NumberConstants.standardBlockWidth)
                    .height(NumberConstants.standardBlockHeight)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = NumberConstants.rowHorizontalPadding,
                        vertical = NumberConstants.rowVerticalPadding
                    ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = block.type.value,
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )
                }
            }
        }

        is PrintBlock -> {
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .width(NumberConstants.standardBlockWidth)
                    .height(NumberConstants.standardBlockHeight)
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = NumberConstants.rowHorizontalPadding,
                        vertical = NumberConstants.rowVerticalPadding
                    ),
                    horizontalArrangement = Arrangement.spacedBy(NumberConstants.rowHorizontalArrangement/2),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = block.type.value,
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    Text(
                        text = stringResource(R.string.simbol3),
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = { block.valueInput.set(it) },
                        modifier = Modifier
                            .width(NumberConstants.ReturnBlock.inputFieldWidth)
                            .height(NumberConstants.inputTextFieldHeight),
                        enabled = !showMenu,
                        singleLine = true,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                        )
                    )

                    Text(
                        text = ")",
                        color = Color.White,
                        fontSize = NumberConstants.textFontSize,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )
                }
            }
        }

        is FunctionBlock -> {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .padding(
                        horizontal = NumberConstants.boxPadding,
                        vertical = NumberConstants.boxPadding
                    )
                    .onGloballyPositioned { coordinates ->
                        onChange(block, density)
                    }
                    .defaultMinSize(
                        minHeight = NumberConstants.wideBlockHeight,
                        minWidth = NumberConstants.wideBlockWidth
                    )
                    .height(block.getDynamicHeightDp(density))
                    .width(block.getDynamicWidthDp(density))

                    .background(
                        color = block.color.value,
                        shape = RoundedCornerShape(NumberConstants.boxRoundedShape)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(NumberConstants.columnPadding),
                    verticalArrangement = Arrangement.spacedBy(NumberConstants.columnVerticalArrangement)
                ) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(NumberConstants.Function.horizontalArrangement),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = NumberConstants.textFontSize,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                            modifier = Modifier.width(NumberConstants.Function.labelWidth)
                        )

                        OutlinedTextField(
                            value = block.nameInput.getInputField(),
                            onValueChange = { block.nameInput.set(it) },
                            modifier = Modifier
                                .width(NumberConstants.Function.inputTextFieldWidth)
                                .height(NumberConstants.Function.inputTextFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                            )
                        )

                        OutlinedTextField(
                            value = block.inputParameters.getInputField(),
                            onValueChange = { block.inputParameters.set(it) },
                            modifier = Modifier
                                .width(NumberConstants.Function.parametersInputTextFieldWidth)
                                .height(NumberConstants.Function.inputTextFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                            )
                        )

                        OutlinedTextField(
                            value = block.returnValueTypeInput.getInputField(),
                            onValueChange = { block.returnValueTypeInput.set(it) },
                            modifier = Modifier
                                .width(NumberConstants.Function.inputTextFieldWidth)
                                .height(NumberConstants.Function.inputTextFieldHeight),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = NumberConstants.inputFieldFontSize, fontFamily = font
                            )
                        )

                        Button(
                            onClick = {
                                if (!showMenu) {
                                    onSwapMenu(block.blocks)
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
                            .height(block.blocks.getDynamicHeightDp(density))
                            .width(block.blocks.getDynamicWidthDp(density))
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
                        block.blocks.blocks.forEach { blockInner ->
                            key(blockInner.id) {
                                Drag(
                                    block = blockInner,
                                    blocksOnScreen = block.blocks.blocks,
                                    blockWithDeleteShownId = innerBlockWithDeleteShownId,
                                    onShowDeleteChange = { id -> innerBlockWithDeleteShownId = id },
                                    onSwapMenu = onSwapMenu,
                                    onChangeSize = { onChange(blockInner, density) })
                            }

                        }
                        Spacer(modifier = Modifier.height(NumberConstants.spacerHeight))
                    }
                }
            }
        }
    }
}