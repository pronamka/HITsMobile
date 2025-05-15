/*package com.example.hit.language

import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.operations.BinaryOperation
import com.example.hit.language.parser.operations.ConditionOperation
import com.example.hit.language.parser.operations.ConditionOperationType

class Main {
    fun main() {
        val atomicValues = """
            5
            [2, 4, 1, 9, 2]
            1
            0
            i
            size
            j
            a[i]
            a[j]
            t
            a
        """.trimIndent()
        println(VariableType.classMap)
        val values = Parser(Lexer(atomicValues).tokenize()).parse()
        println(VariableType.classMap)
        val program: List<IStatement> = listOf(
            DeclarationStatement(
                VariableType.INT, "size", values[0] //5
            ),
            DeclarationStatement(
                VariableType.ARRAY(VariableType.INT, 5), "a", values[1] // array
            ),
            ForLoop(
                DeclarationStatement(
                    VariableType.INT, "i", values[3] // 0
                ),
                ConditionOperation(
                    values[4], values[5], ConditionOperationType.LESS //i, size
                ),
                VariableAssignmentStatement(
                    "i", BinaryOperation(
                        values[4], values[2], TokenType.PLUS //i, 1
                    )
                ),
                BlockStatement(
                    listOf(
                        ForLoop(
                            DeclarationStatement(
                                VariableType.INT, "j", values[4] // i
                            ),
                            ConditionOperation(
                                values[6], values[5], ConditionOperationType.LESS // j, size
                            ),
                            VariableAssignmentStatement(
                                "j", BinaryOperation(
                                    values[6], values[2], TokenType.PLUS // j, 1
                                )
                            ),
                            BlockStatement(
                                listOf(
                                    IfElseStatement(
                                        listOf(
                                            Pair(
                                                ConditionOperation(
                                                    values[7], // a[i]
                                                    values[8], // a[j]
                                                    ConditionOperationType.GREATER
                                                ), BlockStatement(
                                                    listOf(
                                                        DeclarationStatement(
                                                            VariableType.INT, "t", values[7] // a[i]
                                                        ),
                                                        ArrayElementAssignmentStatement(
                                                            "a", values[8], values[4] // a[j], i
                                                        ),
                                                        ArrayElementAssignmentStatement(
                                                            "a", values[9], values[6] // t, j
                                                        ),
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            PrintStatement(values[10])
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
}*/