package com.example.hit.blocks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hit.NumberConstants
import com.example.hit.blocks.container.Container
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
import com.example.hit.language.parser.WhileLoop
import com.example.hit.language.parser.operations.IOperation
import java.util.UUID
import kotlin.math.max


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
    var heightDP: Dp = NumberConstants.standardBlockHeight,
    var widthDP: Dp = NumberConstants.standardBlockWidth,
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

    fun breakBottomConnection() {
        if (bottomConnection != null) {
            bottomConnection!!.topConnection = null
            bottomConnection = null
        }
    }

    abstract fun execute(): IStatement
    abstract fun deepCopy(): BasicBlock

    open fun getDynamicHeightPx(density: Density): Float {
        return with(density) { heightDP.toPx() }
    }

    open fun getDynamicWidthPx(density: Density): Float {
        return with(density) { widthDP.toPx() }
    }

    fun getDynamicHeightDp(density: Density): Dp {
        return with(density) { getDynamicHeightPx(density).toDp() }
    }

    fun getDynamicWidthDp(density: Density): Dp {
        return with(density) { getDynamicWidthPx(density).toDp() }
    }
}

class AssignmentBlock(
    blockId: UUID, color: Color = Color(0xFF45A3FF)
) : BasicBlock(
    id = blockId, type = BlockType.ASSIGNMENT, color = mutableStateOf(color)
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
    blockId: UUID, color: Color = Color(0xFF45A3FF)
) : BasicBlock(
    blockId, type = BlockType.DECLARATION, color = mutableStateOf(color)
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
    blockId: UUID, color: Color = Color(0xFF45A3FF)
) : BasicBlock(
    blockId, type = BlockType.INITIALIZATION, color = mutableStateOf(color)
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
    blockId: UUID,
) : BasicBlock(
    blockId, type = BlockType.PRINT, color = mutableStateOf(Color(0xFF45A3FF))
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
    blockId, type = BlockType.BLOCK, color = mutableStateOf(Color(0xFF45A3FF))
) {
    val blocks = mutableStateListOf<BasicBlock>()

    fun addBlock(block: BasicBlock) {
        this.parentBlock!!.breakBottomConnection()
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

    fun isValidBlockArrangement(): Boolean {
        val container = Container(blocks)
        return container.isValidBlockArrangement()
    }


    fun getLowestPoint(density: Density): Float {
        var height = 0f
        for (block in blocks) {
            height += block.getDynamicHeightPx(density)
        }
        height = max(height, super.getDynamicHeightPx(density))
        return height
    }

    fun getPointToSpawn(density: Density): Float {
        if (blocks.isEmpty()) {
            return 0f
        }
        return getLowestPoint(density) - blocks.last().getDynamicHeightPx(density)
    }

    override fun getDynamicHeightPx(density: Density): Float {
        return getLowestPoint(density) + with(density) { (NumberConstants.bodyBlockVerticalPadding * 2).toPx() }
    }

    override fun getDynamicWidthPx(density: Density): Float {
        var width = 0f
        for (block in blocks) {
            width = max(width, block.getDynamicWidthPx(density))
        }
        return width + with(density) { (NumberConstants.bodyBlockHorizontalPadding * 2 + NumberConstants.borderWidth).toPx() }
    }

    override fun deepCopy(): BasicBlock {
        return BodyBlock(UUID.randomUUID())
    }
}

class IfElseBlock(
    blockId: UUID
) : BasicBlock(
    blockId, type = BlockType.IF, color = mutableStateOf(Color(0xFFBD3FCB))
) {

    var blocksInput = mutableListOf<Pair<OperationInputField, BodyBlock>>()
    var standardHeight =
        (NumberConstants.standardInputFieldHeight + NumberConstants.standardSpacerHeight + NumberConstants.standardColumnHorizontalArrangement * 2 + NumberConstants.standardColumnPadding * 2)

    var standardWidth = NumberConstants.IfElseBlock.rowWidth

    var standardBottomRowHeight =
        (NumberConstants.standardColumnVerticalArrangement * 2 + NumberConstants.standardAddElseBlockButtonHeight)

    init {
        heightDP = NumberConstants.wideBlockHeight
        widthDP = NumberConstants.wideBlockWidth
        addElseIfBlock("")
    }

    var defaultBlockInput: BodyBlock? = null


    override fun getDynamicHeightPx(density: Density): Float {
        var inBox = 0f

        for (blockInput in blocksInput) {
            inBox += blockInput.second.getDynamicHeightPx(density)
            inBox += with(density) { (standardHeight).toPx() }
        }

        if (defaultBlockInput != null) {
            inBox += defaultBlockInput!!.getDynamicHeightPx(density)
            inBox += with(density) { (standardHeight).toPx() }
        } else {
            inBox += with(density) { (standardBottomRowHeight).toPx() }
        }
        inBox = max(inBox, super.getDynamicHeightPx(density))
        return inBox
    }

    override fun getDynamicWidthPx(density: Density): Float {
        var inBox = with(density) { (standardWidth).toPx() }
        if (defaultBlockInput != null) {
            inBox = max(inBox, defaultBlockInput!!.getDynamicHeightPx(density))
        }
        for (blockInput in blocksInput) {
            inBox = max(inBox, blockInput.second.getDynamicWidthPx(density))
        }
        inBox += with(density) { (NumberConstants.IfElseBlock.overallPadding).toPx() }
        inBox = max(inBox, super.getDynamicWidthPx(density))
        return inBox
    }

    fun addElseIfBlock(condition: String) {
        breakBottomConnection()
        val conditionInput = OperationInputField()
        conditionInput.set(condition)
        val block = BodyBlock(blockId = UUID.randomUUID())
        block.parentBlock = this
        blocksInput.add(Pair(conditionInput, block))
    }

    fun setNewCondition(condition: String, index: Int) {
        val conditionInput = OperationInputField()
        conditionInput.set(condition)
        blocksInput[index] = Pair(conditionInput, blocksInput[index].second)
    }

    fun addElseBlock() {
        breakBottomConnection()
        defaultBlockInput = BodyBlock(blockId = UUID.randomUUID())
    }

    override fun execute(): IfElseStatement {
        val blocks = mutableListOf<Pair<IOperation, BlockStatement>>()
        var defaultBlock: BlockStatement? = null

        for (blockInput in blocksInput) {
            val operation = blockInput.first.getOperation()
            blocks.add(Pair(operation, blockInput.second.execute()))
        }

        if (defaultBlockInput != null) {
            defaultBlock = defaultBlockInput!!.execute()
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
    blockId, type = BlockType.FOR, color = mutableStateOf(Color(0xFF7745FF))
) {
    val initializerInput = DeclarationStatementInputField()
    val conditionInput = OperationInputField()
    val stateChangeInput = AssignmentStatementInputField()
    val blocks = BodyBlock(blockId = UUID.randomUUID())
    var standardHeight =
        (NumberConstants.standardBoxPadding * 2 + NumberConstants.standardColumnPadding * 2 + NumberConstants.standardColumnVerticalArrangement * 2 + NumberConstants.ForBlock.inputTextFieldHeight)
    var standardWidth = NumberConstants.ForBlock.rowWidth


    init {
        blocks.parentBlock = this
        heightDP = NumberConstants.wideBlockHeight
        widthDP = NumberConstants.wideBlockWidth
    }

    override fun execute(): ForLoop {
        val initializer = initializerInput.getDeclarationStatement()
        val operation = conditionInput.getOperation()
        val stateChange = stateChangeInput.getAssignmentStatement()
        return ForLoop(initializer, operation, stateChange, blocks.execute())
    }

    override fun deepCopy(): BasicBlock {
        return ForBlock(UUID.randomUUID())
    }

    override fun getDynamicHeightPx(density: Density): Float {
        var inBox = blocks.getDynamicHeightPx(density)
        inBox += with(density) { (standardHeight).toPx() }

        inBox = max(inBox, super.getDynamicHeightPx(density))
        return inBox
    }

    override fun getDynamicWidthPx(density: Density): Float {
        var inBox = max(with(density) { (standardWidth).toPx() }, blocks.getDynamicWidthPx(density))
        inBox += with(density) { (NumberConstants.ForBlock.overallPadding).toPx() }
        inBox = max(inBox, super.getDynamicWidthPx(density))
        return inBox
    }
}

class WhileBlock(
    blockId: UUID
) : BasicBlock(
    blockId, type = BlockType.WHILE, color = mutableStateOf(Color(0xFF7745FF))
) {
    val conditionInput = OperationInputField()
    val blocks = BodyBlock(blockId = UUID.randomUUID())

    var standardHeight =
        (NumberConstants.standardBoxPadding * 2 + NumberConstants.standardColumnPadding * 2 + NumberConstants.standardColumnVerticalArrangement + NumberConstants.WhileBlock.inputTextFieldHeight)
    var standardWidth = NumberConstants.WhileBlock.rowWidth

    init {
        blocks.parentBlock = this
        heightDP = NumberConstants.wideBlockHeight
        widthDP = NumberConstants.wideBlockWidth
    }

    override fun execute(): WhileLoop {
        val operation = conditionInput.getOperation()
        return WhileLoop(operation, blocks.execute())
    }

    override fun deepCopy(): BasicBlock {
        return WhileBlock(UUID.randomUUID())
    }

    override fun getDynamicHeightPx(density: Density): Float {
        var inBox = blocks.getDynamicHeightPx(density)
        inBox += with(density) { (standardHeight).toPx() }

        inBox = max(inBox, super.getDynamicHeightPx(density))
        return inBox
    }

    override fun getDynamicWidthPx(density: Density): Float {
        var inBox = max(with(density) { (standardWidth).toPx() }, blocks.getDynamicWidthPx(density))
        inBox += with(density) { (NumberConstants.WhileBlock.overallPadding).toPx() }
        inBox = max(inBox, super.getDynamicWidthPx(density))
        return inBox
    }
}

class BreakBlock(
    blockId: UUID
) : BasicBlock(
    blockId, type = BlockType.BREAK, color = mutableStateOf(Color(0xFFFF9645))
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
    blockId, type = BlockType.CONTINUE, color = mutableStateOf(Color(0xFFFF9645))
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
    blockId, type = BlockType.RETURN, color = mutableStateOf(Color(0xFFFF9645))
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
    blockId, type = BlockType.FUNCTION, color = mutableStateOf(Color(0xFF45A3FF))
) {
    val nameInput = NameInputField()
    val returnValueTypeInput = TypeInputField()
    val inputParameters = FunctionParametersInputField()
    val blocks = BodyBlock(blockId = UUID.randomUUID())

    var standardHeight =
        (NumberConstants.standardBoxPadding * 2 + NumberConstants.standardColumnPadding * 2 + NumberConstants.standardColumnVerticalArrangement * 2 + NumberConstants.Function.inputTextFieldHeight)
    var standardWidth = NumberConstants.Function.rowWidth

    init {
        blocks.parentBlock = this
        heightDP = NumberConstants.wideBlockHeight
        widthDP = NumberConstants.wideBlockWidth
    }

    override fun getDynamicHeightPx(density: Density): Float {
        var inBox = blocks.getDynamicHeightPx(density)
        inBox += with(density) { (standardHeight).toPx() }

        inBox = max(inBox, super.getDynamicHeightPx(density))
        return inBox
    }

    override fun getDynamicWidthPx(density: Density): Float {
        var inBox = max(with(density) { (standardWidth).toPx() }, blocks.getDynamicWidthPx(density))
        inBox += with(density) { (NumberConstants.Function.overallPadding).toPx() }
        inBox = max(inBox, super.getDynamicWidthPx(density))
        return inBox
    }

    override fun execute(): FunctionDeclarationStatement {
        val name = nameInput.getName()
        val returnValueType = returnValueTypeInput.getType()
        val parameters = inputParameters.getFunctionParametersInputField()
        return FunctionDeclarationStatement(name, parameters, blocks.execute(), returnValueType)
    }

    override fun deepCopy(): BasicBlock {
        return FunctionBlock(UUID.randomUUID())
    }
}