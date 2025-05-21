package com.example.hit

import androidx.compose.ui.graphics.Color

enum class BlockType(val value: String) {

    VARIABLE_INITIALIZATION(""),

    VARIABLE_DECLARATION("variable_declaration"),
    ARRAY_DECLARATION("array_dec"),

    VARIABLE_ASSIGNMENT("variable_as"),
    ARRAY_ELEMENT_ASSIGNMENT("array_elem_as"),

    IF("if"),
    ELSE("else"),
    ELSE_IF("else if"),


    FOR("for"),
    WHILE("while"),

    PLUS("+"),
    MINUS("-"),
    DIVISION("/"),
    MULTY("*"),
    MOD("%"),

    AND("and"),
    OR("or"),
    NOT("not"),

    RETURN("return"),

    PRINT("print"),
    BREAK("break"),
    CONTINUE("continue"),

    BLOCK("block"),
    FUNCTION("function");
}

enum class DataType {
    INT,
    STRING,
    FLOAT,
    BOOLEAN
}

data class BlockPosition(
    var id : Int,
    var posX : Float = 0f,
    var posY : Float = 0f
)

data class CodeBlock(
    val type: BlockType,
    val color: Color,
)

object BlockData {
    val defaultBlocks = listOf(

        CodeBlock(
            type = BlockType.VARIABLE_INITIALIZATION,
            color = Color(0xFF2196F3)
        ),

        CodeBlock(
            type = BlockType.IF,
            color = Color(0xFFA5D6A7)
        ),
        CodeBlock(
            type = BlockType.ELSE,
            color = Color(0xFF81C784)
        ),
        CodeBlock(
            type = BlockType.ELSE_IF,
            color = Color(0xFF66BB6A)
        ),

        CodeBlock(
            type = BlockType.FOR,
            color = Color(0xFFFFCC80)
        ),
        CodeBlock(
            type = BlockType.WHILE,
            color = Color(0xFFFFB74D)
        ),

        CodeBlock(
            type = BlockType.AND,
            color = Color(0xFFC3A2FF)
        ),
        CodeBlock(
            type = BlockType.OR,
            color = Color(0xFFA06BF8)
        ),
        CodeBlock(
            type = BlockType.NOT,
            color = Color(0xFF8F52FF)
        ),

        CodeBlock(
            type = BlockType.RETURN,
            color = Color(0xFFDE30FF)
        ),

        CodeBlock(
            type = BlockType.PRINT,
            color = Color(0xFFFF3C00)
        ),
        CodeBlock(
            type = BlockType.BREAK,
            color = Color(0xFF2D2F2A)
        ),
        CodeBlock(
            type = BlockType.FUNCTION,
            color = Color(0xFF257043)
        ),
        CodeBlock(
            type = BlockType.CONTINUE,
            color = Color(0xFFCFFF00)
        ),
    )
}


