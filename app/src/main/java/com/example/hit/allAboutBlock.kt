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


data class BlockPosition(
    var id : UUID,
    var posX : Float = 0f,
    var posY : Float = 0f
)



