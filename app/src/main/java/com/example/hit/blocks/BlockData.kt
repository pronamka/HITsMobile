package com.example.hit.blocks

import androidx.compose.ui.graphics.Color
import java.util.UUID

object BlockData {
    val defaultBlocks = listOf(
        VariableInitializationBlock(UUID.randomUUID()),
        VariableDeclarationBlock(UUID.randomUUID()),
        ArrayDeclarationBlock(UUID.randomUUID()),
        VariableAssignmentBlock(UUID.randomUUID()),
        ArrayElementAssignmentBlock(UUID.randomUUID()),
        PrintBlock(UUID.randomUUID()),
        BodyBlock(UUID.randomUUID()),
        IfElseBlock(UUID.randomUUID()),
        ForBlock(UUID.randomUUID()),
        WhileBlock(UUID.randomUUID()),
        BreakBlock(UUID.randomUUID()),
        ContinueBlock(UUID.randomUUID()),
        ReturnBlock(UUID.randomUUID()),
        FunctionBlock(UUID.randomUUID())
    )
}