package com.example.hit.blocks

import java.util.UUID

object BlockData {
    val defaultBlocks = listOf(
        VariableInitializationBlock(UUID.randomUUID()),
        ArrayInitializationBlock(UUID.randomUUID()),
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