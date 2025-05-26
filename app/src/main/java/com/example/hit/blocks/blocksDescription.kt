package com.example.hit.blocks

import androidx.compose.ui.graphics.Color
import com.example.hit.BlockPosition
import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.BreakStatement
import com.example.hit.language.parser.ContinueStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.FunctionDeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.WhileLoop
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ReturnOperation
import java.util.UUID

abstract class BasicBlock(
    var id: UUID,
    val type: BlockType,
    val color: Color,
    var topConnection: BasicBlock? = null,
    var bottomConnection: BasicBlock? = null,
) {
    fun move() {
        if (topConnection != null) {
            topConnection!!.bottomConnection = null
            topConnection = null
        }
        if (bottomConnection != null) {
            bottomConnection!!.topConnection = null
            bottomConnection = null
        }
    }
    fun isBottomCompatible(topBlock : BasicBlock) : Boolean{
        return topBlock.bottomConnection == null
    }

    fun isTopCompatible(bottomBlock : BasicBlock) : Boolean{
        return bottomBlock.topConnection == null
    }

    fun connectTopBlock(topBlock : BasicBlock) {
        topConnection = topBlock
        topBlock.bottomConnection = this
    }
    fun connectBottomBlock(bottomBlock : BasicBlock) {
        bottomConnection = bottomBlock
        bottomBlock.topConnection = this
    }

    abstract fun execute(): IStatement
    abstract fun deepCopy(): BasicBlock
}

abstract class DeclarationBlock(
    type: BlockType,
    blockId: UUID,
) : BasicBlock(blockId, type = type, color = Color(0xFF45A3FF)) {
    val nameInput = NameInputField()
    val typeInput = TypeInputField()

    fun getParameters(): Pair<String, VariableType> {
        val name = nameInput.getName()
        val type = typeInput.getType()
        return Pair(name, type)
    }
}

class VariableInitializationBlock(
    blockId: UUID,
) : DeclarationBlock(blockId = blockId, type = BlockType.VARIABLE_INITIALIZATION) {

    override fun execute(): DeclarationStatement {
        val parameters = getParameters()
        return DeclarationStatement(parameters.second, parameters.first)
    }

    override fun deepCopy(): BasicBlock {
        return VariableInitializationBlock(UUID.randomUUID())
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

    override fun deepCopy(): BasicBlock {
        return VariableDeclarationBlock(UUID.randomUUID())
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

    override fun deepCopy(): BasicBlock {
        return ArrayDeclarationBlock(UUID.randomUUID())
    }
}

abstract class AssignmentBlock(
    blockId: UUID,
    type: BlockType,
) : BasicBlock(id = blockId, type = type, color = Color(0xFF45A3FF)) {
    val nameInput = NameInputField()
    val valueInput = OperationInputField()

    fun getParameters(): Pair<String, IOperation> {
        val name = nameInput.getName()
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

    override fun deepCopy(): BasicBlock {
        return VariableAssignmentBlock(UUID.randomUUID())
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

    override fun deepCopy(): BasicBlock {
        return ArrayElementAssignmentBlock(UUID.randomUUID())
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

    override fun deepCopy(): BasicBlock {
        return PrintBlock(UUID.randomUUID())
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

    override fun deepCopy(): BasicBlock {
        return BodyBlock(UUID.randomUUID())
    }
}

class IfElseBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.IF, color = Color(0xFF45A3FF)) {

    val blocksInput = mutableListOf<Pair<OperationInputField, BodyBlock>>()
    private var defaultBlockInput: BodyBlock? = null

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

    override fun deepCopy(): BasicBlock {
        return IfElseBlock(UUID.randomUUID())
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

    override fun deepCopy(): BasicBlock {
        return ForBlock(UUID.randomUUID())
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

    override fun deepCopy(): BasicBlock {
        return WhileBlock(UUID.randomUUID())
    }
}

class BreakBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.BREAK, color = Color(0xFF45A3FF)) {
    override fun execute(): BreakStatement {
        return BreakStatement()
    }

    override fun deepCopy(): BasicBlock {
        return BreakBlock(UUID.randomUUID())
    }
}

class ContinueBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.CONTINUE, color = Color(0xFF45A3FF)) {
    override fun execute(): ContinueStatement {
        return ContinueStatement()
    }

    override fun deepCopy(): BasicBlock {
        return ContinueBlock(UUID.randomUUID())
    }
}

class ReturnBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.RETURN, color = Color(0xFF45A3FF)) {
    val valueInputField = OperationInputField()

    override fun execute(): ReturnStatement {
        return ReturnStatement(ReturnOperation(valueInputField.getOperation()))
    }

    override fun deepCopy(): BasicBlock {
        return ReturnBlock(UUID.randomUUID())
    }
}

class FunctionBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.FUNCTION, color = Color(0xFF45A3FF)) {
    val nameInput = NameInputField()
    val inputParameters = mutableListOf<VariableInitializationBlock>()
    val blocks = BodyBlock(blockId = UUID.randomUUID())

    override fun execute(): FunctionDeclarationStatement {
        val name = nameInput.getName()
        val statements = mutableListOf<IStatement>()
        val parameters = mutableListOf<DeclarationStatement>()
        for (block in blocks.blocks){
            statements.add(block.execute())
        }
        for (inputParameter in inputParameters){
            parameters.add(inputParameter.execute())
        }
        return FunctionDeclarationStatement(name, parameters, BlockStatement(statements))
    }

    override fun deepCopy(): BasicBlock {
        return FunctionBlock(UUID.randomUUID())
    }

    fun addParameter(initBlock: VariableInitializationBlock) {
        inputParameters.add(initBlock)
    }
}