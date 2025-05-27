package com.example.hit.language

import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.FunctionCallStatement
import com.example.hit.language.parser.FunctionDeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.FunctionCallOperation
import com.example.hit.language.parser.operations.IOperation

fun getOperation(input: String): IOperation {
    return Parser(Lexer(input).tokenize()).parse()[0]
}

class Main {
    fun main() {
        val program: List<IStatement> = listOf(
            FunctionDeclarationStatement(
                "printArray",
                listOf(
                    DeclarationStatement(
                        VariableType.ARRAY(VariableType.INT), "arr"
                    ),
                ),
                BlockStatement(
                    mutableListOf(
                        ForLoop(
                            DeclarationStatement(
                                VariableType.INT, "i", getOperation("0")
                            ),
                            ComparisonOperation(
                                getOperation("i"), getOperation("5"), TokenType.LESS
                            ),
                            VariableAssignmentStatement(
                                "i", getOperation("i+1")
                            ),
                            BlockStatement(
                                mutableListOf(
                                    PrintStatement(getOperation("arr[i]"))
                                )
                            )
                        ),
                        ArrayElementAssignmentStatement(
                            "arr", getOperation("arr[1]"), getOperation("0")
                        ),
                        ReturnStatement(getOperation("arr"))
                    )
                ),
                VariableType.ARRAY(VariableType.INT)
            ),
            DeclarationStatement(
                VariableType.ARRAY(VariableType.INT, getOperation("5")),
                "a",
                getOperation("[5, 4, 3, 2, 1]")
            ),
            DeclarationStatement(
                VariableType.ARRAY(VariableType.INT, getOperation("5")),
                "b",
                getOperation("printArray(a)")
            ),
            PrintStatement(getOperation("a")),
            PrintStatement(getOperation("b.size()")),
        )
        for (statement in program) {
            println(statement)
        }
        for (statement in program) {
            statement.evaluate()
        }
    }
}

fun main(args: Array<String>) {
    Main().main()

}