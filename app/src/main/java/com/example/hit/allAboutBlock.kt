package com.example.hit

import androidx.compose.ui.graphics.Color
import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.BoolValue
import com.example.hit.language.parser.BreakStatement
import com.example.hit.language.parser.ContinueStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.FunctionDeclarationStatement
import com.example.hit.language.parser.FunctionValue
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.Scopes
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.WhileLoop
import com.example.hit.language.parser.exceptions.ContinueIterationException
import com.example.hit.language.parser.exceptions.StopIterationException
import com.example.hit.language.parser.exceptions.UnexpectedTypeException
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ReturnOperation
import java.util.UUID
import kotlin.uuid.Uuid

enum class BlockType(val value: String) {

    VARIABLE_INITIALIZATION(""),

    VARIABLE_DECLARATION("variable_declaration"),
    ARRAY_DECLARATION("array_dec"),

    VARIABLE_ASSIGNMENT("variable_as"),
    ARRAY_ELEMENT_ASSIGNMENT("array_elem_as"),

    IF("if"),

    FOR("for"),
    WHILE("while"),

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
    var id : UUID,
    var posX : Float = 0f,
    var posY : Float = 0f
)

//data class CodeBlock(
//    val id: Int,
//    val type: BlockType,
//    val color: Color,
//    val relatedBlock: BasicBlock? = null
//)
//
//object BlockData {
//    val defaultBlocks = listOf(
//        CodeBlock(
//            id = 1,
//            type = BlockType.VARIABLE_INITIALIZATION,
//            color = Color(0xFF2196F3)
//        ),
//        CodeBlock(
//            id = 2,
//            type = BlockType.IF,
//            color = Color(0xFFA5D6A7)
//        ),
//        CodeBlock(
//            id = 3,
//            type = BlockType.ELSE,
//            color = Color(0xFF81C784)
//        ),
//        CodeBlock(
//            id = 4,
//            type = BlockType.ELSE_IF,
//            color = Color(0xFF66BB6A)
//        ),
//        CodeBlock(
//            id = 5,
//            type = BlockType.FOR,
//            color = Color(0xFFFFCC80)
//        ),
//        CodeBlock(
//            id = 6,
//            type = BlockType.WHILE,
//            color = Color(0xFFFFB74D)
//        ),
//        CodeBlock(
//            id = 9,
//            type = BlockType.NOT,
//            color = Color(0xFF8F52FF)
//        ),
//        CodeBlock(
//            id = 10,
//            type = BlockType.RETURN,
//            color = Color(0xFFDE30FF)
//        ),
//        CodeBlock(
//            id = 11,
//            type = BlockType.PRINT,
//            color = Color(0xFF03A9F4)
//        ),
//        CodeBlock(
//            id = 12,
//            type = BlockType.BREAK,
//            color = Color(0xFF2D2F2A)
//        ),
//        CodeBlock(
//            id = 13,
//            type = BlockType.FUNCTION,
//            color = Color(0xFF257043)
//        ),
//        CodeBlock(
//            id = 14,
//            type = BlockType.CONTINUE,
//            color = Color(0xFFCFFF00)
//        )
//    )
//}



