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
    val relatedBlock: BasicBlock? = null
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

class BodyBlock(
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

class IfElseStatement(
    val blocks: List<Pair<IOperation, BlockStatement>>,
    val defaultBlock: BlockStatement? = null,
) : IStatement {
    override fun evaluate() {
        for ((condition, block) in blocks) {
            val conditionValue = condition.evaluate()
            if (conditionValue !is BoolValue){
                throw UnexpectedTypeException("Expected a BoolValue, but got ${conditionValue::class.java.simpleName}")
            }
            if (conditionValue.value) {
                block.evaluate()
                return
            }
        }
        defaultBlock?.evaluate()
    }
}

class IfElseBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.IF, color = Color(0xFF45A3FF)) {

    val blocksInput = mutableListOf<Pair<OperationInputField, BodyBlock>>()
    var defaultBlockInput: BodyBlock? = null

    fun addElseIfBlock() {
        val conditionInput = OperationInputField()
        val block = BodyBlock(blockId = UUID.randomUUID())
        blocksInput.add(Pair(conditionInput, block))
    }

    fun addElseBlock() {
        defaultBlockInput = BodyBlock(blockId = UUID.randomUUID())
    }

    override fun execute(): IfElseStatement {
        val blocks = mutableListOf<Pair<IOperation, BlockStatement>>()
        var defaultBlock: BlockStatement? = null

        for (blockInput in blocksInput) {
            val operation = blockInput.first.getOperation()
            val statements = mutableListOf<IStatement>()
            for (block in blockInput.second.blocks){
                statements.add(block.execute())
            }
            blocks.add(Pair(operation, BlockStatement(statements)))
        }

        if (defaultBlockInput != null) {
            val statements = mutableListOf<IStatement>()
            for (block in defaultBlockInput!!.blocks) {
                statements.add(block.execute())
            }
            defaultBlock = BlockStatement(statements)
        }

        return IfElseStatement(blocks, defaultBlock)
    }
}

class ForBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.FOR, color = Color(0xFF45A3FF)) {
    val initializer = VariableAssignmentBlock(blockId = UUID.randomUUID())
    val conditionInput = OperationInputField()
    val stateChange = VariableAssignmentBlock(blockId = UUID.randomUUID())
    val blocks = BodyBlock(blockId = UUID.randomUUID())

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
    val blocks = BodyBlock(blockId = UUID.randomUUID())

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
    val blocks = BodyBlock(blockId = UUID.randomUUID())

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


