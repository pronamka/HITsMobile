package com.example.hit.language

import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.operations.BinaryOperation
import com.example.hit.language.parser.operations.IOperation
import com.example.hit.language.parser.operations.ConditionalOperation
import com.example.hit.language.parser.operations.ConditionalOperationType

class Main{
    fun main(){
        val atomicValues = "2\n2\na\n10\na\n2"
        val values = Parser(Lexer(atomicValues).tokenize()).parse()
        val program: List<IOperation> = listOf(
            DeclarationStatement(
                VariableType.INT,
                "a"
            ),
            AssignmentStatement(
                "a",
                BinaryOperation(
                    values[0], values[1], TokenType.PLUS
                ),
            ),
            BinaryOperation(
                values[2], values[3], TokenType.ASTERISK
            ),
            ConditionalOperation(
                values[4], values[5], ConditionalOperationType.NOT_EQUAL
            )
        )
        for (statement in program){
            println(statement.evaluate())
        }
    }
}

fun main(args: Array<String>){
    Main().main()
}