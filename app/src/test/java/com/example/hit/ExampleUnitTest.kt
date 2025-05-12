package com.example.hit

import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.Variable
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.operations.BinaryOperation
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertEquals


class LanguageTester {
    @Test
    fun testDeclaration() {
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val atomic_values = """
            2
            2.5
            "hello world"
            "true"
            a
            b
            c
            d
        """.trimIndent()
        val values = Parser(Lexer(atomic_values).tokenize()).parse()
        val program: List<IStatement> = listOf(
            DeclarationStatement(
                VariableType.INT,
                "a",
                values[0]
            ),
            DeclarationStatement(
                VariableType.DOUBLE,
                "b",
                values[1]
            ),
            DeclarationStatement(
                VariableType.STRING,
                "c",
                values[2]
            ),
            DeclarationStatement(
                VariableType.BOOL,
                "d",
                values[3]
            ),
            PrintStatement(values[4]),
            PrintStatement(values[5]),
            PrintStatement(values[6]),
            PrintStatement(values[7]),
        )

        val expectedOutput = """
            2
            2.5
            hello world
            BoolValue: true
        """.trimIndent()

        for (statement in program) {
            statement.evaluate()
        }

        val output = outputStream.toString().replace("\r", "").trim()
        assertEquals(expectedOutput, output, "Expected output was: $expectedOutput; \n" +
                "Actual outputs was: $output")
    }

    @Test
    fun testArrays(){
        val outputStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outputStream))

        val atomic_values = """
            "2, 4, 6, 7"
            a
        """.trimIndent()
        val values = Parser(Lexer(atomic_values).tokenize()).parse()
        val program: List<IStatement> = listOf(
            DeclarationStatement(
                VariableType.ARRAY(VariableType.INT, 4),
                "a",
                values[0]
            ),
            PrintStatement(values[1])
        )

        val expectedOutput = """Array Value: Size 4, Elements: [2, 4, 6, 7]""".trimIndent()

        for (statement in program) {
            statement.evaluate()
        }

        val output = outputStream.toString().replace("\r", "").trim()
        assertEquals(expectedOutput, output, "Expected output was: $expectedOutput; \n" +
                "Actual outputs was: $output")
    }
}