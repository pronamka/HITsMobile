package com.example.hit.blocks

import androidx.compose.ui.graphics.Color
import com.example.hit.BlockPosition
import com.example.hit.BlockType
import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.ArrayToken
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.BreakStatement
import com.example.hit.language.parser.ContinueStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.FunctionDeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.ValueOperationFactory
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.WhileLoop
import com.example.hit.language.parser.operations.IOperation
import java.util.UUID

abstract class BasicBlock(
    val id: UUID,
    val type: BlockType,
    val color: Color,
    val logicalPosition: Int? = null,
    val position: BlockPosition? = null
) {
    open var compatibleBlocks: List<BlockType> = BlockType.entries
    abstract fun execute(): IStatement
}

abstract class InitializationBlock(
    type: BlockType,
    blockId: UUID,
) : BasicBlock(blockId, type = type, color = Color(0xFF45A3FF)) {
    private val nameInput = NameInputField()
    private val valueInput = OperationInputField()

    fun getParameters(): Pair<String, IOperation> {
        val name = nameInput.getName()
        val value = valueInput.getOperation()
        return Pair(name, value)
    }
}

class VariableInitializationBlock(
    blockId: UUID,
) : InitializationBlock(blockId = blockId, type = BlockType.VARIABLE_DECLARATION) {
    override fun execute(): VariableAssignmentStatement {
        val parameters = getParameters()
        return VariableAssignmentStatement(parameters.first, parameters.second)
    }
}

class ArrayInitializationBlock(
    blockId: UUID,
) : InitializationBlock(blockId = blockId, type = BlockType.ARRAY_DECLARATION) {

    private val sizeInput = OperationInputField()

    override fun execute(): VariableAssignmentStatement {
        val size = sizeInput.getOperation()
        val parameters = getParameters()
        return VariableAssignmentStatement(
            parameters.first,
            ValueOperationFactory(ArrayToken(size, parameters.second)).create()
        )
    }
}

abstract class AssignmentBlock(
    blockId: UUID,
    type: BlockType,
) : BasicBlock(id = blockId, type = type, color = Color(0xFF45A3FF)) {
    private val nameInput = NameInputField()
    private val valueInput = OperationInputField()


    fun getParameters(): Pair<String, IOperation> {
        val name = nameInput.getName()
        val operation = valueInput.getOperation()
        return Pair(name, operation)
    }
}

class ArrayElementAssignmentBlock(
    blockId: UUID
) : AssignmentBlock(blockId = blockId, type = BlockType.ARRAY_ELEMENT_ASSIGNMENT) {

    private val indexInput = OperationInputField()

    override fun execute(): ArrayElementAssignmentStatement {
        val parameters = getParameters()
        val index = indexInput.getOperation()
        return ArrayElementAssignmentStatement(parameters.first, parameters.second, index)
    }
}

class PrintBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.PRINT, color = Color(0xFF45A3FF)) {
    private val valueInput = OperationInputField()

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
        for (block in blocks) {
            statements.add(block.execute())
        }
        return BlockStatement(statements)
    }
}

class IfElseBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.IF, color = Color(0xFF45A3FF)) {

    private val blocksInput = mutableListOf<Pair<OperationInputField, BodyBlock>>()
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
            for (block in blockInput.second.blocks) {
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
    private val initializer = VariableInitializationBlock(blockId = UUID.randomUUID())
    private val conditionInput = OperationInputField()
    private val stateChange = VariableInitializationBlock(blockId = UUID.randomUUID())
    private val blocks = BodyBlock(blockId = UUID.randomUUID())

    override fun execute(): ForLoop {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks) {
            statements.add(block.execute())
        }
        return ForLoop(
            initializer.execute(),
            operation,
            stateChange.execute(),
            BlockStatement(statements)
        )
    }
}


class WhileBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.WHILE, color = Color(0xFF45A3FF)) {
    private val conditionInput = OperationInputField()
    private val blocks = BodyBlock(blockId = UUID.randomUUID())

    override fun execute(): WhileLoop {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks) {
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
    private val valueInputField = OperationInputField()

    override fun execute(): ReturnStatement {
        return ReturnStatement(valueInputField.getOperation())
    }
}

class FunctionBlock(
    blockId: UUID,
) : BasicBlock(blockId, type = BlockType.FUNCTION, color = Color(0xFF45A3FF)) {
    private val nameInput = NameInputField()
    private val inputParameters = StringInputField()
    private val blocks = BodyBlock(blockId = UUID.randomUUID())

    override fun execute(): FunctionDeclarationStatement {
        val name = nameInput.getName()
        val statements = mutableListOf<IStatement>()
        val parameters = mutableListOf<String>()
        for (block in blocks.blocks) {
            statements.add(block.execute())
        }
        for (inputParameter in inputParameters.value!!.split(",")) {
            if (inputParameter.isEmpty()){
                break
            }
            parameters.add(inputParameter.trim())
        }
        return FunctionDeclarationStatement(name, parameters, BlockStatement(statements))
    }
}