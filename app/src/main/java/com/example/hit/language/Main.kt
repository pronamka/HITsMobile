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
import com.example.hit.language.parser.operations.IOperation

fun getOperation(input: String): IOperation {
    return Parser(Lexer(input).tokenize()).parse()[0]
}

class Main {
    fun main() {
        println(VariableType.classMap)
        val program: List<IStatement> = listOf(
            FunctionDeclarationStatement(
                "bubbleSort",
                listOf(
                    DeclarationStatement(
                        VariableType.INT, "array_size"
                    ),
                    DeclarationStatement(
                        VariableType.ARRAY(VariableType.INT, getOperation("5")), "arr"
                    )
                ),
                BlockStatement(
                    mutableListOf(
                        ForLoop(
                            DeclarationStatement(
                                VariableType.INT, "i", getOperation("0")
                            ),
                            getOperation("i<array_size"),
                            VariableAssignmentStatement(
                                "i", getOperation("i+1")
                            ),
                            BlockStatement(
                                mutableListOf(
                                    ForLoop(
                                        DeclarationStatement(
                                            VariableType.INT, "j", getOperation("i")
                                        ),
                                        getOperation("j<array_size"),
                                        VariableAssignmentStatement(
                                            "j", getOperation("j+1")
                                        ),
                                        BlockStatement(
                                            mutableListOf(
                                                IfElseStatement(
                                                    listOf(
                                                        Pair(
                                                            getOperation("arr[i]>arr[j]"),
                                                            BlockStatement(
                                                                mutableListOf(
                                                                    DeclarationStatement(
                                                                        VariableType.INT,
                                                                        "t",
                                                                        getOperation("arr[i]")
                                                                    ),
                                                                    ArrayElementAssignmentStatement(
                                                                        "arr",
                                                                        getOperation("arr[j]"),
                                                                        getOperation("i")
                                                                    ),
                                                                    ArrayElementAssignmentStatement(
                                                                        "arr",
                                                                        getOperation("t"),
                                                                        getOperation("j")
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
                        ReturnStatement(
                            getOperation("arr")
                        )
                    ),
                    isFunctionBody = true
                ),
            ),
            DeclarationStatement(
                VariableType.INT, "size", getOperation("5")
            ),
            DeclarationStatement(
                VariableType.ARRAY(VariableType.INT, getOperation("size")),
                "unsorted_array",
                getOperation("[5, 4, 3, 2, 1]")
            ),
            DeclarationStatement(
                VariableType.ARRAY(VariableType.INT, getOperation("size")),
                "sorted_array",
                getOperation("bubbleSort(size, unsorted_array)")
            ),
            PrintStatement(
                getOperation("unsorted_array")
            ),
            PrintStatement(
                getOperation("sorted_array")
            )
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