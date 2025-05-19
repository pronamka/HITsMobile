package com.example.hit.language

import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.FunctionDeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.operations.BinaryOperation
import com.example.hit.language.parser.operations.ComparisonOperation
import com.example.hit.language.parser.operations.ConditionOperationType

class Main {
    fun main() {
        val atomicValues = """
            a+b
            2
            3
            sum(first, second)
            a
            b
        """.trimIndent()
        println(VariableType.classMap)
        val values = Parser(Lexer(atomicValues).tokenize()).parse()
        println(VariableType.classMap)
        val program: List<IStatement> = listOf(
            FunctionDeclarationStatement(
                "sum", listOf(
                    DeclarationStatement(
                        VariableType.INT, "a"
                    ),
                    DeclarationStatement(
                        VariableType.INT, "b"
                    )
                ),
                BlockStatement(
                    mutableListOf(
                        IfElseStatement(
                            listOf(
                                Pair(
                                    ComparisonOperation(
                                        values[4], values[5], TokenType.LESS
                                    ),
                                    BlockStatement(
                                        mutableListOf(
                                            ReturnStatement(
                                                values[0]
                                            )
                                        )
                                    )
                                )
                            )
                        ),
                        ReturnStatement(
                            values[5]
                        )
                    )
                )
            ),
            DeclarationStatement(
                VariableType.INT, "first", values[1]
            ),
            DeclarationStatement(
                VariableType.INT, "second", values[2]
            ),
            PrintStatement(values[3])
        )
        println(VariableType.classMap)
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