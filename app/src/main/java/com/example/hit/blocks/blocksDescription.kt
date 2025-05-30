package com.example.hit.blocks

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hit.BlockPosition
import com.example.hit.Constants
import com.example.hit.blocks.container.Container
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
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.StatementsParser
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.WhileLoop
import com.example.hit.language.parser.operations.IOperation
import java.util.UUID

val blockTypeToColor = mapOf(
    BlockType.ASSIGNMENT to Color(0xFF45A3FF),
    BlockType.DECLARATION to Color(0xFF45A3FF),
    BlockType.INITIALIZATION to Color(0xFF45A3FF),
    BlockType.PRINT to Color(0xFF45A3FF),
    BlockType.BLOCK to Color(0xFF45A3FF),
    BlockType.FUNCTION to Color(0xFF45A3FF),
    BlockType.IF to Color(0xFFBD3FCB),
    BlockType.FOR to Color(0xFF7745FF),
    BlockType.WHILE to Color(0xFF7745FF),
    BlockType.BREAK to Color(0xFFFF9645),
    BlockType.CONTINUE to Color(0xFFFF9645),
    BlockType.RETURN to Color(0xFFFF9645)
)

abstract class BasicBlock(
    var id: UUID,
    val type: BlockType,
    var color: MutableState<Color>,
    var x: Float = 0f,
    var y: Float = 0f,
    var topConnection: BasicBlock? = null,
    var bottomConnection: BasicBlock? = null,
    var heightDP: Dp = 80.dp,
    var widthDP: Dp = 256.dp,
    var zIndex: Float = 0f,
    var parentBlock: BasicBlock? = null
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

    fun isBottomCompatible(): Boolean {
        return bottomConnection == null
    }

    fun isTopCompatible(): Boolean {
        return topConnection == null
    }

    fun connectTopBlock(topBlock: BasicBlock) {
        topConnection = topBlock
        topBlock.bottomConnection = this
    }

    fun connectBottomBlock(bottomBlock: BasicBlock) {
        bottomConnection = bottomBlock
        bottomBlock.topConnection = this
    }

    abstract fun execute(): IStatement
    abstract fun deepCopy(): BasicBlock

    open fun getDynamicHeightPx(density: Density): Float {
        return with(density) { heightDP.toPx() }
    }

    open fun getDynamicWidthPx(density: Density): Float {
        return with(density) { widthDP.toPx() }
    }
}

class AssignmentBlock(
    blockId: UUID,
    color: Color = Color(0xFF45A3FF)
) : BasicBlock(
    id = blockId,
    type = BlockType.ASSIGNMENT,
    color = mutableStateOf(color)
) {
    val nameInput = StringInputField()
    val valueInput = OperationInputField()

    override fun execute(): AssignmentStatement {
        val name = nameInput.get()
        val value = valueInput.getInputField()
        val statement = "$name = $value"
        return StatementsParser(Lexer(statement).tokenize()).parseAssignment()
    }

    override fun deepCopy(): BasicBlock {
        return AssignmentBlock(UUID.randomUUID(), color.value)
    }
}

class DeclarationBlock(
    blockId: UUID,
    color: Color = Color(0xFF45A3FF)
) : BasicBlock(
    blockId,
    type = BlockType.DECLARATION,
    color = mutableStateOf(color)
) {
    val nameInput = NameInputField()
    val typeInput = TypeInputField()

    override fun execute(): DeclarationStatement {
        val name = nameInput.getName()
        val type = typeInput.getType()
        return DeclarationStatement(type, name)
    }

    override fun deepCopy(): BasicBlock {
        return DeclarationBlock(UUID.randomUUID(), color.value)
    }
}

class InitializationBlock(
    blockId: UUID,
    color: Color = Color(0xFF45A3FF)
) : BasicBlock(
    blockId,
    type = BlockType.INITIALIZATION,
    color = mutableStateOf(color)
) {
    val nameInput = NameInputField()
    val typeInput = TypeInputField()
    val valueInput = OperationInputField()

    override fun execute(): DeclarationStatement {
        val name = nameInput.getName()
        val type = typeInput.getType()
        val value = valueInput.getOperation()
        return DeclarationStatement(type, name, value)
    }

    override fun deepCopy(): BasicBlock {
        return InitializationBlock(UUID.randomUUID(), color.value)
    }
}

class PrintBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.PRINT,
    color = mutableStateOf(Color(0xFF45A3FF))
) {
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
) : BasicBlock(
    blockId,
    type = BlockType.BLOCK,
    color = mutableStateOf(Color(0xFF45A3FF))
) {
    val blocks = mutableListOf<BasicBlock>()

    fun addBlock(block: BasicBlock) {
        block.parentBlock = this
        blocks.add(block)
    }

    override fun execute(): BlockStatement {
        val container = Container(blocks)
        val orderedBlocks = container.getOrderedBlocks()
        val statements = mutableListOf<IStatement>()
        for (block in orderedBlocks) {
            statements.add(block.execute())
        }
        return BlockStatement(statements)
    }

    override fun getDynamicHeightPx(density: Density): Float {
        var height = 0f
        for (block in blocks) {
            height += block.getDynamicHeightPx(density)
        }
        return height + with(density) {(Constants.bodyBlockVerticalPadding * 2).toPx()}
    }

    override fun getDynamicWidthPx(density: Density): Float{
        var width = 0f
        for (block in blocks) {
            if (width < block.getDynamicWidthPx(density))
                width = block.getDynamicWidthPx(density)
        }
        return width + with(density) {(Constants.bodyBlockHorizontalPadding * 2).toPx()}
    }

    override fun deepCopy(): BasicBlock {
        return BodyBlock(UUID.randomUUID())
    }
}

class IfElseBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.IF,
    color = mutableStateOf(Color(0xFFBD3FCB))
) {
    var blocksInput = mutableListOf<Pair<OperationInputField, BodyBlock>>()

    init {
        addElseIfBlock("")
    }

    var defaultBlockInput: BodyBlock? = null


    override fun getDynamicHeightPx(density: Density): Float {
        var inBox = 0f
        inBox += with(density) {(Constants.standardColumnPadding * 2).toPx()}
        for (blockInput in blocksInput) {
            inBox += blockInput.second.getDynamicHeightPx(density)
            inBox += with(density) {(Constants.standardColumnHorizontalArrangement).toPx()}
        }
        /*if (defaultBlockInput != null) {
            inBox += defaultBlockInput!!.getDynamicHeightPx(density)
        }*/
        return inBox
    }

    override fun getDynamicWidthPx(density: Density): Float {
        var inBox = 0f
        for (blockInput in blocksInput) {
            if (blockInput.second.getDynamicWidthPx(density)>inBox){
                inBox = blockInput.second.getDynamicWidthPx(density)
            }
        }
        /*if (defaultBlockInput != null) {
            inBox += defaultBlockInput!!.getDynamicHeightPx(density)
        }*/
        return inBox
    }

    fun addElseIfBlock(condition: String) {
        val conditionInput = OperationInputField()
        conditionInput.set(condition)
        val block = BodyBlock(blockId = UUID.randomUUID(), )
        block.parentBlock = this
        blocksInput.add(Pair(conditionInput, block))
    }

    fun setNewCondition(condition: String, index: Int) {
        val conditionInput = OperationInputField()
        conditionInput.set(condition)
        blocksInput[index] = Pair(conditionInput, blocksInput[index].second)
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

    override fun deepCopy(): BasicBlock {
        return IfElseBlock(UUID.randomUUID())
    }
}

class ForBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.FOR,
    color = mutableStateOf(Color(0xFF7745FF))
) {
    val initializerInput = DeclarationStatementInputField()
    val conditionInput = OperationInputField()
    val stateChangeInput = AssignmentStatementInputField()
    val blocks = BodyBlock(blockId = UUID.randomUUID())

    override fun execute(): ForLoop {
        val initializer = initializerInput.getDeclarationStatement()
        val operation = conditionInput.getOperation()
        val stateChange = stateChangeInput.getAssignmentStatement()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks) {
            statements.add(block.execute())
        }
        return ForLoop(initializer, operation, stateChange, BlockStatement(statements))
    }

    override fun deepCopy(): BasicBlock {
        return ForBlock(UUID.randomUUID())
    }
}

class WhileBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.WHILE,
    color = mutableStateOf(Color(0xFF7745FF))
) {
    val conditionInput = OperationInputField()
    val blocks = BodyBlock(blockId = UUID.randomUUID())

    override fun execute(): WhileLoop {
        val operation = conditionInput.getOperation()
        val statements = mutableListOf<IStatement>()
        for (block in blocks.blocks) {
            statements.add(block.execute())
        }
        return WhileLoop(operation, BlockStatement(statements))
    }

    override fun deepCopy(): BasicBlock {
        return WhileBlock(UUID.randomUUID())
    }
}

class BreakBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.BREAK,
    color = mutableStateOf(Color(0xFFFF9645))
) {
    override fun execute(): BreakStatement {
        return BreakStatement()
    }

    override fun deepCopy(): BasicBlock {
        return BreakBlock(UUID.randomUUID())
    }
}

class ContinueBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.CONTINUE,
    color = mutableStateOf(Color(0xFFFF9645))
) {
    override fun execute(): ContinueStatement {
        return ContinueStatement()
    }

    override fun deepCopy(): BasicBlock {
        return ContinueBlock(UUID.randomUUID())
    }
}

class ReturnBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.RETURN,
    color = mutableStateOf(Color(0xFFFF9645))
) {
    val valueInputField = OperationInputField()

    override fun execute(): ReturnStatement {
        return ReturnStatement((valueInputField.getOperation()))
    }

    override fun deepCopy(): BasicBlock {
        return ReturnBlock(UUID.randomUUID())
    }
}

class FunctionBlock(
    blockId: UUID
) : BasicBlock(
    blockId,
    type = BlockType.FUNCTION,
    color = mutableStateOf(Color(0xFF45A3FF))
) {
    val nameInput = NameInputField()
    val returnValueTypeInput = TypeInputField()
    val inputParameters = FunctionParametersInputField()
    val blocks = BodyBlock(blockId = UUID.randomUUID())

    override fun execute(): FunctionDeclarationStatement {
        val name = nameInput.getName()
        val returnValueType = returnValueTypeInput.getType()
        val statements = mutableListOf<IStatement>()
        val parameters = inputParameters.getFunctionParametersInputField()
        for (block in blocks.blocks) {
            statements.add(block.execute())
        }
        return FunctionDeclarationStatement(name, parameters, BlockStatement(statements), returnValueType)
    }

    override fun deepCopy(): BasicBlock {
        return FunctionBlock(UUID.randomUUID())
    }
}