package com.example.hit.language

import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.FunctionDeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.OperationsParser
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.StatementsParser
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.IOperation

fun getOperation(input: String): IOperation {
    return OperationsParser(Lexer(input).tokenize()).parse()[0]
}

fun getDeclarationStatement(input: String): DeclarationStatement{
    return StatementsParser(Lexer(input).tokenize()).parseDeclaration()
}

fun getAssignmentStatement(input: String): AssignmentStatement{
    return StatementsParser(Lexer(input).tokenize()).parseAssignment()
}

fun getStatementsParser(input: String): StatementsParser{
    return StatementsParser(Lexer(input).tokenize())
}

class Main {
    fun main() {
        val program: List<IStatement> = listOf(
            FunctionDeclarationStatement(
                "printArray",
                getStatementsParser("arr Int[]").parseFunctionParameters(),
                BlockStatement(
                    mutableListOf(
                        ForLoop(
                            getDeclarationStatement("i Int = 0"),
                            ComparisonOperation(
                                getOperation("i"), getOperation("5"), TokenType.LESS
                            ),
                            getAssignmentStatement("i = i+1"),
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
                getStatementsParser("Int[]").parseType(),
            ),
            getDeclarationStatement("a Int[5] = [5, 4, 3, 2, 1]"),
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