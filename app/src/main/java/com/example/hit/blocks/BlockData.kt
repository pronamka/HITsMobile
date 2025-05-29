package com.example.hit.blocks

import androidx.compose.ui.graphics.Color
import java.util.UUID

object BlockData {
    val defaultBlocks = listOf(
        InitializationBlock(UUID.randomUUID()),
        DeclarationBlock(UUID.randomUUID()),
        AssignmentBlock(UUID.randomUUID()),
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