package com.example.hit

import androidx.compose.ui.graphics.Color
import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.BlockStatement
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
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ReturnOperation
import java.util.UUID
import kotlin.uuid.Uuid

enum class BlockType(string: String) {
    VARIABLE_INITIALIZATION("variable_initialization"),

    VARIABLE_DECLARATION("variable_declaration"),
    ARRAY_DECLARATION("array_declaration"),

    VARIABLE_ASSIGNMENT("variable_assignment"),
    ARRAY_ELEMENT_ASSIGNMENT("array_element_assignment"),

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

    PRINT("print"),

    BREAK("break"),
    CONTINUE("continue"),

    BLOCK("block"),

    FUNCTION("function")
    
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
    val id: UUID,
    val type: BlockType,
    val color: Color,
    val position: BlockPosition? = null
) {
    open var compatibleBlocks: List<BlockType> = BlockType.entries
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

abstract class DeclarationBlock(
    type : BlockType,
    blockId: UUID,
) : BasicBlock(blockId, type = type, color = Color(0xFF45A3FF)) {
    val nameInput = StringInputField()
    val typeInput = StringInputField()

    val stringToTypeMap = mapOf(
        "Int" to VariableType.INT,
        "String" to VariableType.STRING,
        "Bool" to VariableType.BOOL,
        "Double" to VariableType.DOUBLE
    )
    
    fun getParameters(): Pair<String, VariableType>{
        val name = nameInput.get()
        val type = typeInput.get()
        if (!stringToTypeMap.containsKey(type)) {
            throw Exception("wrong type")
        }
        return Pair(name, stringToTypeMap[type]!!)
    }
}

class VariableInitializationBlock(
    blockId: UUID,
) : DeclarationBlock(blockId = blockId, type = BlockType.VARIABLE_INITIALIZATION) {

    override fun execute(): DeclarationStatement {
        val parameters = getParameters()
        return DeclarationStatement(parameters.second, parameters.first)
    }
}

class VariableDeclarationBlock(
    blockId: UUID,
) : DeclarationBlock(blockId = blockId, type = BlockType.VARIABLE_DECLARATION) {

    val valueInput = OperationInputField()

    override fun execute(): DeclarationStatement {
        val value = valueInput.getOperation()
        val parameters = getParameters()
        return DeclarationStatement(parameters.second, parameters.first, value)
    }
}

class ArrayDeclarationBlock(
    blockId: UUID,
) : DeclarationBlock(blockId = blockId, type = BlockType.ARRAY_DECLARATION) {

    val valueInput = OperationInputField()
    val sizeInput = OperationInputField()
    
    override fun execute(): DeclarationStatement {
        val size = sizeInput.getOperation()
        val value = valueInput.getOperation()
        val parameters = getParameters()
        return DeclarationStatement(VariableType.ARRAY(parameters.second, size), parameters.first, value)
    }
}

abstract class AssignmentBlock(
    blockId: UUID,
    type: BlockType,
) : BasicBlock(id = blockId, type = type, color = Color(0xFF45A3FF)) {
    val nameInput = StringInputField()
    val valueInput = OperationInputField()


    fun getParameters(): Pair<String, IOperation>{
        val name = nameInput.get()
        val operation = valueInput.getOperation()
        return Pair(name, operation)
    }
}

class VariableAssignmentBlock(
    blockId: UUID
) : AssignmentBlock(blockId = blockId, type = BlockType.VARIABLE_ASSIGNMENT) {
    override fun execute(): AssignmentStatement {
        val parameters = getParameters()
        return VariableAssignmentStatement(parameters.first, parameters.second)
    }
}

class ArrayElementAssignmentBlock(
    blockId: UUID
) : AssignmentBlock(blockId = blockId, type = BlockType.ARRAY_ELEMENT_ASSIGNMENT) {
    
    val indexInput = OperationInputField()
    
    override fun execute(): ArrayElementAssignmentStatement {
        val parameters = getParameters()
        val index = indexInput.getOperation()
        return ArrayElementAssignmentStatement(parameters.first, parameters.second, index)
    }
}

class PrintBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.PRINT, color = Color(0xFF45A3FF)) {
    val valueInput = OperationInputField()

    override fun execute(): PrintStatement {
        val operation = valueInput.getOperation()
        return PrintStatement(operation)
    }
}

class BlockBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.BLOCK, color = Color(0xFF45A3FF)) {
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
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.IF, color = Color(0xFF45A3FF)) {
    var conditionInput = OperationInputField()
    val blocks = BlockBlock(blockId = UUID.randomUUID())
    

    override fun execute(): IfElseStatement {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks){
            statements.add(block.execute())
        }
        return IfElseStatement(listOf(Pair(operation, BlockStatement(statements))))
    }
}

class ElifBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.ELSE_IF, color = Color(0xFF45A3FF)) {
    val conditionInput = OperationInputField()
    val blocks = BlockBlock(blockId = UUID.randomUUID())

    override fun execute(): IfElseStatement {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks){
            statements.add(block.execute())
        }
        return IfElseStatement(listOf(Pair(operation, BlockStatement(statements))))
    }
}

class ElseBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.ELSE, color = Color(0xFF45A3FF)) {
    val blocks = BlockBlock(blockId = UUID.randomUUID())

    override fun execute(): BlockStatement {
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks){
            statements.add(block.execute())
        }
        return BlockStatement(statements)
    }
}

class ForBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.FOR, color = Color(0xFF45A3FF)) {
    val initializer = VariableAssignmentBlock(blockId = UUID.randomUUID())
    val conditionInput = OperationInputField()
    val stateChange = VariableAssignmentBlock(blockId = UUID.randomUUID())
    val blocks = BlockBlock(blockId = UUID.randomUUID())

    override fun execute(): ForLoop {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks){
            statements.add(block.execute())
        }
        return ForLoop(initializer.execute(), operation, stateChange.execute(), BlockStatement(statements))
    }
}


class WhileBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.WHILE, color = Color(0xFF45A3FF)) {
    val conditionInput = OperationInputField()
    val blocks = BlockBlock(blockId = UUID.randomUUID())

    override fun execute(): WhileLoop {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks){
            statements.add(block.execute())
        }
        return WhileLoop(operation, BlockStatement(statements))
    }
}

class BreakBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.BREAK, color = Color(0xFF45A3FF)) {
    override fun execute(): BreakStatement {
        return BreakStatement()
    }
}

class ContinueBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.CONTINUE, color = Color(0xFF45A3FF)) {
    override fun execute(): ContinueStatement {
        return ContinueStatement()
    }
}

class ReturnBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.RETURN, color = Color(0xFF45A3FF)) {
    val valueInputField = OperationInputField()
    
    override fun execute(): ReturnStatement{
        return ReturnStatement(ReturnOperation(valueInputField.getOperation()))
    }
}

class FunctionBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.FUNCTION, color = Color(0xFF45A3FF)) {
    val nameInput = StringInputField()
    val inputParameters = mutableListOf<VariableInitializationBlock>()
    val blocks = BlockBlock(blockId = UUID.randomUUID())

    override fun execute(): FunctionDeclarationStatement {
        val name = nameInput.get()
        val statements = mutableListOf<IStatement>()
        val parameters = mutableListOf<DeclarationStatement>()
        for (block in blocks.blocks){
            statements.add(block.execute())
        }
        for (inputParameter in inputParameters){
            parameters.add(inputParameter.execute())
        }
        return FunctionDeclarationStatement (name, parameters, BlockStatement(statements))
    }

    fun addParameter(initBlock: VariableInitializationBlock) {
        inputParameters.add(initBlock)
    }
}

class AssignmentArrayElementBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.VARIABLE_ASSIGNMENT, color = Color(0xFF45A3FF)) {
    val indexInput = OperationInputField()
    val nameInput = StringInputField()
    val valueInput = OperationInputField()

    override fun execute(): ArrayElementAssignmentStatement{
        val name = nameInput.get()
        val operation = valueInput.getOperation()
        val index = indexInput.getOperation()
        return ArrayElementAssignmentStatement(name, operation, index)
    }
}


