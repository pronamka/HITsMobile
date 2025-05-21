package com.example.hit.language

import com.example.hit.language.parser.ArrayElementAssignmentStatement
import com.example.hit.language.parser.ArrayToken
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.BlockStatement
import com.example.hit.language.parser.VariableAssignmentStatement
import com.example.hit.language.parser.ForLoop
import com.example.hit.language.parser.FunctionDeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.ReturnStatement
import com.example.hit.language.parser.ValueOperationFactory
import com.example.hit.language.parser.operations.IOperation

fun getOperation(input: String): IOperation {
    return Parser(Lexer(input).tokenize()).parse()[0]
}

class Main {
    fun main() {
        val program: List<IStatement> = listOf(
            FunctionDeclarationStatement(
                "bubbleSort",
                listOf(
                    "array_size", "arr"
                ),
                BlockStatement(
                    mutableListOf(
                        ForLoop(
                            VariableAssignmentStatement(
                                "i", getOperation("0")
                            ),
                            getOperation("i<array_size"),
                            VariableAssignmentStatement(
                                "i", getOperation("i+1")
                            ),
                            BlockStatement(
                                mutableListOf(
                                    ForLoop(
                                        VariableAssignmentStatement(
                                            "j", getOperation("i")
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
                                                                    VariableAssignmentStatement(
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
            VariableAssignmentStatement(
                "size", getOperation("5")
            ),
            VariableAssignmentStatement(
                "unsorted_array",
                getOperation("array(size)")
            ),
            ForLoop(
                VariableAssignmentStatement("i", getOperation("0")),
                getOperation("i<size"),
                VariableAssignmentStatement("i", getOperation("i+1")),
                BlockStatement(
                    mutableListOf(
                        ArrayElementAssignmentStatement(
                            "unsorted_array",
                            getOperation("size-i"),
                            getOperation("i")
                        )
                    )
                )
            ),
            VariableAssignmentStatement(
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