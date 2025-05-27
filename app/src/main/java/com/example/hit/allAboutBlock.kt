package com.example.hit

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hit.blocks.ArrayDeclarationBlock
import com.example.hit.blocks.ArrayElementAssignmentBlock
import com.example.hit.blocks.BasicBlock
import com.example.hit.blocks.BreakBlock
import com.example.hit.blocks.ContinueBlock
import com.example.hit.blocks.ForBlock
import com.example.hit.blocks.IfElseBlock
import com.example.hit.blocks.PrintBlock
import com.example.hit.blocks.ReturnBlock
import com.example.hit.blocks.VariableAssignmentBlock
import com.example.hit.blocks.VariableDeclarationBlock
import com.example.hit.blocks.VariableInitializationBlock
import com.example.hit.blocks.WhileBlock
import java.util.UUID


data class BlockPosition(
    var id : UUID,
    var posX : Float = 0f,
    var posY : Float = 0f
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockItem(
    showMenu : Boolean = false,
    block: BasicBlock,
    onClick: () -> Unit,
    onHeightChanged: (Dp) -> Unit,
) {
    var isDataTypeDropdownExpanded by remember { mutableStateOf(false) }

    val dataTypes = remember {
        listOf(
            "Int",
            "String",
            "Boolean",
            "Float",
            "Double",
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

    when (block) {
        is VariableDeclarationBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = { block.nameInput.setName(it)},
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
                    Text(text = ":",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = isDataTypeDropdownExpanded,
                            onExpandedChange = {
                                if (!showMenu) isDataTypeDropdownExpanded = it
                            }
                        ) {
                            OutlinedTextField(
                                value = block.typeInput.getInputField(),
                                onValueChange = { block.typeInput.setType(it) },
                                readOnly = true,
                                enabled = !showMenu,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = isDataTypeDropdownExpanded
                                    )
                                },
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
                                            block.typeInput.setType(type)
                                            isDataTypeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(text = "=",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = {
                            block.valueInput.setOperation(it)
                        },
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
        }

        is IfElseBlock -> {
            val density = LocalDensity.current
            var hasElse by remember { mutableStateOf(false) }
            var elseIfCounts by remember { mutableStateOf(listOf<String>()) }

            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .onGloballyPositioned { coordinates ->
                        val newHeight = with(density) { coordinates.size.height.toDp() }
                        block.heightDP = newHeight
                        onHeightChanged(newHeight)
                    }
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .defaultMinSize(minHeight = 80.dp, minWidth = 252.dp)
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = block.type.value,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font,
                        )
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .wrapContentWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .wrapContentHeight(unbounded = true)
                            .defaultMinSize(minHeight = 80.dp, minWidth = 252.dp)
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


                    elseIfCounts.forEachIndexed { index, condition ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(20.dp)
                                ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ELSE IF",
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = font,
                                )
                                OutlinedTextField(
                                    value = condition,
                                    onValueChange = { newCondition ->
                                        elseIfCounts = elseIfCounts.toMutableList().also {
                                            it[index] = newCondition
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth(0.5f)
                                        .height(56.dp),
                                    enabled = !showMenu,
                                    singleLine = true,
                                    colors = textFieldColors
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
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

                    if (hasElse) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .wrapContentHeight(unbounded = true)
                                .defaultMinSize(minHeight = 60.dp, minWidth = 252.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ELSE",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = font,
                            )
                            Box(
                                modifier = Modifier
                                    .padding(8.dp)
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

                    else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                modifier = Modifier
                                    .width(104.dp)
                                    .height(48.dp),
                                onClick = { elseIfCounts = elseIfCounts + "" },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7943DE))
                            ){
                                Text("ELSE IF", color = Color.White, fontFamily = font, fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                modifier = Modifier
                                    .width(104.dp)
                                    .height(48.dp),
                                onClick = { hasElse = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF7943DE)
                                )
                            ) {
                                Text("ELSE", color = Color.White, fontFamily = font, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }

        is VariableInitializationBlock ->{
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
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
                            block.nameInput.setName(it);
                        },
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
                    Text(
                        text = ":",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = isDataTypeDropdownExpanded,
                            onExpandedChange = {
                                if (!showMenu) isDataTypeDropdownExpanded = it
                            }
                        ) {
                            OutlinedTextField(
                                value = block.typeInput.getInputField(),
                                onValueChange = { block.typeInput.setType(it) },
                                readOnly = true,
                                enabled = !showMenu,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = isDataTypeDropdownExpanded
                                    )
                                },
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
                                            block.typeInput.setType(type)
                                            isDataTypeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        is VariableAssignmentBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = {
                            block.nameInput.setName(it);
                        },
                        modifier = Modifier
                            .width(84.dp)
                            .height(56.dp),
                        singleLine = true,
                        enabled = !showMenu,
                        colors = textFieldColors,
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = font
                        )

                    )

                    Text(text = "=",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = { block.valueInput.setOperation(it) },
                        modifier = Modifier
                            .width(84.dp)
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
        }
        is ForBlock -> {
            val density = LocalDensity.current
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
                    .onGloballyPositioned { coordinates ->
                        val newHeight = with(density) { coordinates.size.height.toDp() }
                        block.heightDP = newHeight
                        onHeightChanged(newHeight)
                    }
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
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

                        Text(text = "(",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier
                                .width(72.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = font
                            )
                        )

                        Text(text = ";",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.conditionInput.getInputField(),
                            onValueChange = { block.conditionInput.setOperation(it)},
                            modifier = Modifier
                                .width(72.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = font
                            )
                        )

                        Text(text = ";",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            modifier = Modifier
                                .width(72.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                fontFamily = font
                            )
                        )

                        Text(text = ")",
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
                        val newHeight = with(density) { coordinates.size.height.toDp() }
                        block.heightDP = newHeight
                        onHeightChanged(newHeight)
                    }
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
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

                        Text(text = "(",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.conditionInput.getInputField(),
                            onValueChange = { block.conditionInput.setOperation(it) },
                            modifier = Modifier
                                .width(146.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = font
                            )
                        )

                        Text(text = ")",
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
        is ReturnBlock, is ContinueBlock, is BreakBlock ->{
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
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
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
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

                        Text(text = "(",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )

                        OutlinedTextField(
                            value = block.valueInput.getInputField(),
                            onValueChange = { block.valueInput.setOperation(it) },
                            modifier = Modifier
                                .width(126.dp)
                                .height(56.dp),
                            enabled = !showMenu,
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                fontFamily = font
                            )
                        )

                        Text(text = ")",
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = font
                        )
                    }
                }
            }
        }
        is ArrayDeclarationBlock ->{
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = {
                            block.nameInput.setName(it);
                        },
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
                    Text(text = ":",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        ExposedDropdownMenuBox(
                            expanded = isDataTypeDropdownExpanded,
                            onExpandedChange = {
                                if (!showMenu) isDataTypeDropdownExpanded = it
                            }
                        ) {
                            OutlinedTextField(
                                value = block.typeInput.getInputField(),
                                onValueChange = { block.typeInput.setType(it) },
                                readOnly = true,
                                enabled = !showMenu,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = isDataTypeDropdownExpanded
                                    )
                                },
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
                                            block.typeInput.setType(type)
                                            isDataTypeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Text(text = "[",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.sizeInput.getInputField(),
                        onValueChange = {
                            block.sizeInput.setOperation(it)
                        },
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

                    Text(text = "]",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )
                }
            }
        }
        is ArrayElementAssignmentBlock -> {
            block.heightDP = 80.dp
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .width(252.dp)
                    .height(80.dp)
                    .background(
                        color = block.color,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .then(if (showMenu) Modifier.clickable(onClick = onClick) else Modifier)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = block.nameInput.getInputField(),
                        onValueChange = {
                            block.nameInput.setName(it);
                        },
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

                    Text(text = "[",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.indexInput.getInputField(),
                        onValueChange = {
                            block.indexInput.setOperation(it)
                        },
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

                    Text(
                        text = "]",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    Text(
                        text = "=",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = font
                    )

                    OutlinedTextField(
                        value = block.valueInput.getInputField(),
                        onValueChange = {
                            block.valueInput.setOperation(it)
                        },
                        modifier = Modifier
                            .width(92.dp)
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
        }
    }
}