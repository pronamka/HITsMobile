package com.example.hit

import androidx.compose.ui.graphics.Color


enum class BlockType(string: String) {
    INIT("="),

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


    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_AND_EQUALS(">="),
    LESS_THAN_AND_EQUALS("<="),

    RETURN("return")
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
    var variableName: String = "",
    var dataType: DataType? = null,
    var value: String = ""
)


object BlockData {
    val defaultBlocks = listOf(

        CodeBlock(
            type = BlockType.INIT,
            color = Color(0xFF45A3FF)
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
            type = BlockType.PLUS,
            color = Color(0xFFE1BEE7)
        ),
        CodeBlock(
            type = BlockType.MINUS,
            color = Color(0xFFCE93D8)
        ),
        CodeBlock(
            type = BlockType.DIVISION,
            color = Color(0xFFBA68C8)
        ),
        CodeBlock(
            type = BlockType.MULTY,
            color = Color(0xFFAB47BC)
        ),
        CodeBlock(
            type = BlockType.MOD,
            color = Color(0xFF9C27B0)
        ),


        CodeBlock(
            type = BlockType.AND,
            color = Color(0xFFFFAB91)
        ),
        CodeBlock(
            type = BlockType.OR,
            color = Color(0xFFFF8A65)
        ),
        CodeBlock(
            type = BlockType.NOT,
            color = Color(0xFFFF7043)
        ),


        CodeBlock(
            type = BlockType.EQUALS,
            color = Color(0xFFFFAEBE)
        ),
        CodeBlock(
            type = BlockType.NOT_EQUALS,
            color = Color(0xFFFF80AA)
        ),
        CodeBlock(
            type = BlockType.GREATER_THAN,
            color = Color(0xFFF84983)
        ),
        CodeBlock(
            type = BlockType.LESS_THAN,
            color = Color(0xFFFF3C7E)
        ),
        CodeBlock(
            type = BlockType.GREATER_THAN_AND_EQUALS,
            color = Color(0xFFFA1464)
        ),
        CodeBlock(
            type = BlockType.LESS_THAN_AND_EQUALS,
            color = Color(0xFF930032)
        ),

        CodeBlock(
            type = BlockType.RETURN,
            color = Color(0xFFDE30FF)
        ),
    )
}