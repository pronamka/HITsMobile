package com.example.hit

import androidx.compose.ui.graphics.Color
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.operations.IOperation

enum class BlockType(string: String) {
    INIT("="),
    ASSIGNMENT("="),

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

    RETURN("return"),

    PRINT("print")
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
    val relatedBlock: BasicBlock? = null
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

abstract class BasicBlock(
    val id: Int,
    val type: BlockType,
    val color: Color,
) {
    abstract fun execute(): IStatement
}

class StringInputField{
    var value: String? = null
    fun get(): String{
        if (value == null){
            throw Exception()
        }
        return value as String
    }
}

class OperationInputField{
    val inputFiled: StringInputField = StringInputField()

    fun getOperation(): IOperation{
        val s = inputFiled.get()
        return Parser(Lexer(s).tokenize()).parse()[0]
    }
}

class InitBlock(
    blockId: Int,
) : BasicBlock(blockId, type = BlockType.INIT, color = Color(0xFF45A3FF)) {
    val nameInput = StringInputField()
    val typeInput = StringInputField()
    val valueInput = OperationInputField()

    val stringToTypeMap = mapOf(
        "Int" to VariableType.INT
    )

    override fun execute(): IStatement {
        val name = nameInput.get()
        val type = typeInput.get()
        if (!stringToTypeMap.containsKey(type)) {
            throw Exception()
        }
        val operation = valueInput.getOperation()
        return DeclarationStatement(stringToTypeMap[type]!!, name, operation)
    }
}

class AssignmentBlock(
    blockId: Int,
) : BasicBlock(blockId, type = BlockType.ASSIGNMENT, color = Color(0xFF45A3FF)) {
    val nameInput = StringInputField()
    val valueInput = OperationInputField()

    override fun execute(): IStatement {
        val name = nameInput.get()
        val operation = valueInput.getOperation()
        return VariableAssignmentStatement(name, operation)
    }
}

class PrintBlock(
    blockId: Int,
) : BasicBlock(blockId, type = BlockType.PRINT, color = Color(0xFF45A3FF)) {
    val valueInput = OperationInputField()

    override fun execute(): PrintStatement {
        val operation = valueInput.getOperation()
        return PrintStatement(operation)
    }
}

class BlockBlock(
    blockId: Int,
) : BasicBlock(blockId, type = BlockType.IF, color = Color(0xFF45A3FF)) {
    val blocks = mutableListOf<BasicBlock>()

    override fun execute(): BlockStatement {
        val statements = mutableListOf<IStatement>()
        for (block in blocks){
            statements.add(block.execute())
        }
        return BlockStatement(statements)
    }
}


class IfBlock(
    blockId: Int,
) : BasicBlock(blockId, type = BlockType.IF, color = Color(0xFF45A3FF)) {
    val conditionInput = OperationInputField()
    val blocks = BlockBlock(id = uuid)

    override fun execute(): IfElseStatement {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks){
            statements.add(block.execute())
        }
        return IfElseStatement(listOf(Pair(operation, BlockStatement(statements))))
    }
}




