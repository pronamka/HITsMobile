package com.example.hit

import androidx.annotation.RestrictTo
import com.example.hit.language.parser.AssignmentStatement
import com.example.hit.language.parser.DeclarationStatement
import com.example.hit.language.parser.IStatement
import com.example.hit.language.parser.IfElseStatement
import com.example.hit.language.parser.Lexer
import com.example.hit.language.parser.Parser
import com.example.hit.language.parser.PrintStatement
import com.example.hit.language.parser.Scopes
import com.example.hit.language.parser.TokenType
import com.example.hit.language.parser.Variable
import com.example.hit.language.parser.VariableType
import com.example.hit.language.parser.operations.BinaryOperation
import org.jetbrains.annotations.TestOnly
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.assertEquals


/*class LanguageTester {
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
}*/

class BlockTester {

    companion object {
        @JvmStatic
        @BeforeClass
        fun setUp(): Unit {
            println(VariableType.classMap)
        }
    }

    fun checkOutput(expected: String, actual: String) {
        assertEquals(
            expected,
            actual,
            "Expected output was: $expected; Actual output: $actual"
        )
    }


    @Test
    fun testDeclaration() {
        var init = InitBlock("1")
        init.nameInput.value = "a"
        init.typeInput.value = "Int"
        init.valueInput.inputFiled.value = "2+2"
        val statement = init.execute()
        checkOutput(
            "Declaration Statement: Declaring variable of type INT with name " +
                    "a and value BinaryOperation: ValueOperation: 2 PLUS ValueOperation: 2",
            statement.toString()
        )
        statement.evaluate()
    }

    @Test
    fun testAssignment() {
        testDeclaration()
        var assignment = AssignmentBlock("2")
        assignment.nameInput.value = "a"
        assignment.valueInput.inputFiled.value = "a-2"
        val statement = assignment.execute()
        checkOutput(
            "Assignment Statement: Assigning value BinaryOperation: VariableOperation: a. MINUS ValueOperation: 2 to a.",
            statement.toString()
        )
        statement.evaluate()
    }

    @Test
    fun testPrint() {
        testAssignment()
        var print = PrintBlock("3")
        print.valueInput.inputFiled.value = "a-2"
        val statement: PrintStatement = print.execute()
        checkOutput(
            "Print statement: Printing BinaryOperation: VariableOperation: a. MINUS ValueOperation: 2",
            statement.toString()
        )
        statement.evaluate()
        checkOutput(
            "0",
            statement.outputValue!!
        )
    }

    @Test
    fun testIf() {
        var myIf = IfBlock("4")
        testDeclaration()
        myIf.conditionInput.inputFiled.value = "a>5"
        myIf.blocks.blocks.add(AssignmentBlock)
        val statement: IfElseStatement = myIf.execute()
        println(statement.toString())
        statement.evaluate()

        checkOutput(
            "0",
            statement.outputValue!!
        )
    }



    @After
    fun clearVariables() {
        Scopes.reset()
    }
}