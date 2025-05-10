package com.example.hit.language

import androidx.compose.material3.ProvideTextStyle
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.operations.BinaryOperation
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.WhileLoop
import com.example.hit.language.parser.operations.ConditionOperation
import com.example.hit.language.parser.operations.ConditionOperationType

class Main {
    fun main() {
        val atomicValues = "2\n2\na\n10\na\n2\n2.5\nb\n" +
                "\"a is greater than 2\"\n" +
                "1\n0\ni\n" +
                "\"i is equal to \""
        val values = Parser(Lexer(atomicValues).tokenize()).parse()
        val program: List<IStatement> = listOf(
            DeclarationStatement(
                VariableType.INT,
                "a"
            ),
            DeclarationStatement(
                VariableType.DOUBLE,
                "b",
                values[0]
            ),
            PrintStatement(values[7]),
            AssignmentStatement(
                "b",
                values[3]
            ),
            AssignmentStatement(
                "a",
                BinaryOperation(
                    values[0], values[1], TokenType.PLUS
                ),
            ),
            PrintStatement(values[2]),
            IfElseStatement(
                listOf(
                    Pair(
                        ConditionOperation(values[2], values[1], ConditionOperationType.EQUAL),
                        BlockStatement(
                            listOf(
                                AssignmentStatement(
                                    "a",
                                    BinaryOperation(
                                        values[2], values[1], TokenType.PLUS
                                    ),
                                ),
                                PrintStatement(values[2])
                            ),
                        ),
                    ),
                    Pair(
                        ConditionOperation(values[2], values[1], ConditionOperationType.GREATER),
                        BlockStatement(
                            listOf(
                                PrintStatement(values[8])
                            ),
                        ),
                    ),
                ),
                BlockStatement(
                    listOf(
                        AssignmentStatement(
                            "a",
                            BinaryOperation(
                                values[0], values[2], TokenType.MINUS
                            ),
                        ),
                        PrintStatement(values[2])
                    ),
                )
            ),
            WhileLoop(
                ConditionOperation(
                    values[2], values[3], ConditionOperationType.LESS
                ),
                BlockStatement(
                    listOf(
                        AssignmentStatement(
                            "a",
                            BinaryOperation(
                                values[2],
                                values[9],
                                TokenType.PLUS
                            )
                        ),
                        PrintStatement(
                            values[2]
                        )
                    )
                )
            ),
            ForLoop(
                DeclarationStatement(
                    VariableType.INT,
                    "i",
                    values[10]
                ),
                ConditionOperation(
                    values[11],
                    values[3],
                    ConditionOperationType.LESS
                ),
                AssignmentStatement(
                    "i",
                    BinaryOperation(
                        values[11], //i
                        values[9], //1
                        TokenType.PLUS
                    ),
                ),
                BlockStatement(
                    listOf(
                        PrintStatement(
                            values[12]
                        ),
                        PrintStatement(
                            values[11]
                        )
                    )
                )
            )
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